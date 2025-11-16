package com.devpulse.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import com.devpulse.model.User;
import com.devpulse.Service.WeeklySummaryService;

@Controller
public class WeeklySummaryController {

    @Autowired
    private WeeklySummaryService weeklySummaryService;

    @GetMapping("/summary")
    public String showWeeklySummary(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        var summaryData = weeklySummaryService.getWeeklySummary(user);

        model.addAttribute("pageTitle", "Weekly Summary");
        model.addAttribute("topHeading", "Your Weekly Productivity Pulse");
        model.addAllAttributes(summaryData);

        return "weekly-summary";
    }
}
