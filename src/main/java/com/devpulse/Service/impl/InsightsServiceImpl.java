package com.devpulse.Service.impl;

import com.devpulse.dao.DailyLogRepository;
import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import com.devpulse.Service.InsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightsServiceImpl implements InsightsService {

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Override
    public Map<String, Object> getLongTermInsights(User user) {
        List<DailyLog> logs = dailyLogRepository.findByUserOrderByLogDateDesc(user);
        Map<String, Object> insights = new HashMap<>();

        if (logs.isEmpty()) {
            insights.put("message", "No data available. Start adding daily logs to get insights!");
            return insights;
        }

        // Group logs by week number
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<Integer, List<DailyLog>> logsByWeek = logs.stream()
                .collect(Collectors.groupingBy(log -> log.getLogDate().get(weekFields.weekOfWeekBasedYear())));

        // Average focus per week
        Map<Integer, Double> weeklyAvg = new TreeMap<>();
        for (var entry : logsByWeek.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(DailyLog::getFocusScore)
                    .average().orElse(0);
            weeklyAvg.put(entry.getKey(), avg);
        }

        insights.put("weeklyLabels", weeklyAvg.keySet().stream().map(w -> "Week " + w).toList());
        insights.put("weeklyScores", new ArrayList<>(weeklyAvg.values()));

        // Most and least focused day overall
        Map<String, Double> avgByDay = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getLogDate().getDayOfWeek().toString(),
                        Collectors.averagingInt(DailyLog::getFocusScore)
                ));

        String bestDay = avgByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");

        String worstDay = avgByDay.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");
        
        double avgFocus = logs.stream().mapToInt(DailyLog::getFocusScore).average().orElse(0);
        
        insights.put("averageFocus", String.format("%.1f", avgFocus));
        insights.put("totalLogs", logs.size());
        insights.put("pulseStatus", getPulseStatus(avgFocus));
        insights.put("mostProductiveDay", capitalizeDay(bestDay));
        insights.put("leastFocusedDay", capitalizeDay(worstDay));

        // Productivity streak
        long streak = calculateProductiveStreak(logs);
        insights.put("streak", streak);

        // Personalized insight text
        insights.put("habitInsights", generateHabitInsights(bestDay, worstDay, streak));

        return insights;
    }

    private long calculateProductiveStreak(List<DailyLog> logs) {
        List<DailyLog> sorted = logs.stream()
                .sorted(Comparator.comparing(DailyLog::getLogDate))
                .toList();

        long currentStreak = 0, longestStreak = 0;
        LocalDate prev = null;

        for (DailyLog log : sorted) {
            if (prev != null && log.getLogDate().equals(prev.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }
            longestStreak = Math.max(longestStreak, currentStreak);
            prev = log.getLogDate();
        }
        return longestStreak;
    }

    private List<String> generateHabitInsights(String bestDay, String worstDay, long streak) {
        List<String> tips = new ArrayList<>();
        tips.add("⭐ You perform best on " + capitalizeDay(bestDay) + ". Try scheduling deep work sessions on this day.");
        tips.add("⚠️ " + capitalizeDay(worstDay) + " tends to be your least focused day — keep tasks lighter then.");
        tips.add("🔥 You’ve maintained a productivity streak of " + streak + " day(s). Keep that momentum going!");
        tips.add("💡 Consider reviewing blockers on your low-focus days to identify distractions.");
        return tips;
    }

    private String capitalizeDay(String day) {
        if (day == null || day.isEmpty()) return "";
        return day.charAt(0) + day.substring(1).toLowerCase();
    }
    private String getPulseStatus(double avg) {
        if (avg >= 8) return "Excellent 🚀";
        if (avg >= 6.5) return "Rising 🔼";
        if (avg >= 5) return "Stable ⚖️";
        return "Needs Recharge 🔻";
    }
}
