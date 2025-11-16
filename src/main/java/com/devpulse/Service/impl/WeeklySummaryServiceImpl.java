package com.devpulse.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.devpulse.dao.DailyLogRepository;
import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import com.devpulse.Service.WeeklySummaryService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklySummaryServiceImpl implements WeeklySummaryService {

    @Autowired
    private DailyLogRepository dailyLogRepository;
    @Override
    public Map<String, Object> getWeeklySummary(User user) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        LocalDate twoWeeksAgo = today.minusDays(13);

        List<DailyLog> allLogs = dailyLogRepository.findByUserOrderByLogDateDesc(user);

        // Filter for this week and last week
        List<DailyLog> thisWeek = allLogs.stream()
                .filter(log -> !log.getLogDate().isBefore(weekAgo))
                .collect(Collectors.toList());

        List<DailyLog> lastWeek = allLogs.stream()
                .filter(log -> log.getLogDate().isBefore(weekAgo) && !log.getLogDate().isBefore(twoWeeksAgo))
                .collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        if (thisWeek.isEmpty()) {
            summary.put("insights", List.of("No logs yet this week — add entries to get insights."));
            return summary;
        }

        // Calculate averages
        double avgFocusThisWeek = thisWeek.stream().mapToInt(DailyLog::getFocusScore).average().orElse(0);
        double avgFocusLastWeek = lastWeek.stream().mapToInt(DailyLog::getFocusScore).average().orElse(0);

        summary.put("averageFocus", String.format("%.1f", avgFocusThisWeek));

        // Weekly change
        double change = avgFocusThisWeek - avgFocusLastWeek;
        summary.put("focusChange", String.format("%.1f", change));

        // Find most productive and least focused days
        Map<String, Double> dailyAvg = thisWeek.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getLogDate().getDayOfWeek().toString(),
                        Collectors.averagingInt(DailyLog::getFocusScore)
                ));

        String bestDay = dailyAvg.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");

        String worstDay = dailyAvg.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");

        summary.put("mostProductiveDay", bestDay);
        summary.put("leastFocusedDay", worstDay);

        // Productive days = focus ≥ 7
        long productiveDays = dailyAvg.values().stream().filter(f -> f >= 7).count();
        summary.put("productiveDays", productiveDays);

        // Chart data (chronological)
        Map<LocalDate, Double> orderedTrend = thisWeek.stream()
                .collect(Collectors.groupingBy(DailyLog::getLogDate, TreeMap::new, Collectors.averagingInt(DailyLog::getFocusScore)));

        summary.put("dates", orderedTrend.keySet().stream().map(LocalDate::toString).toList());
        summary.put("focusTrend", new ArrayList<>(orderedTrend.values()));

        // Generate Insights
        List<String> insights = generateInsights(avgFocusThisWeek, avgFocusLastWeek, bestDay, worstDay, productiveDays);
        summary.put("insights", insights);

        return summary;
    }

    // 🧠 Rule-based AI-like insight generation
    private List<String> generateInsights(double thisWeek, double lastWeek, String bestDay, String worstDay, long productiveDays) {
        List<String> insights = new ArrayList<>();

        if (lastWeek == 0)
            insights.add("Great start! This is your first tracked week — keep logging daily.");
        else if (thisWeek > lastWeek)
            insights.add(String.format("🚀 Focus improved by %.1f points from last week. Nice consistency!", thisWeek - lastWeek));
        else if (thisWeek < lastWeek)
            insights.add(String.format("⚠️ Focus dropped by %.1f points this week — check what changed mid-week.", lastWeek - thisWeek));
        else
            insights.add("💡 Focus remained steady compared to last week.");

        insights.add(String.format("⭐ Most productive day: %s — schedule complex tasks then.", capitalizeDay(bestDay)));
        insights.add(String.format("😴 Least focused day: %s — consider reducing meetings or distractions.", capitalizeDay(worstDay)));
        insights.add(String.format("💼 Productive days this week: %d/7.", productiveDays));

        if (thisWeek >= 8)
            insights.add("🔥 Excellent focus level! You’re in peak flow mode.");
        else if (thisWeek >= 6)
            insights.add("🧩 Good job! Try small breaks to push focus a bit higher.");
        else
            insights.add("🕓 Low average focus — maybe review your schedule or workspace setup.");

        return insights;
    }

    private String capitalizeDay(String day) {
        if (day == null || day.isEmpty()) return "";
        return day.charAt(0) + day.substring(1).toLowerCase();
    }

}
