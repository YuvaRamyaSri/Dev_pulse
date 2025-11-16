package com.devpulse.Controller;

import com.devpulse.model.User;
import com.devpulse.Service.InsightsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InsightsController {

    @Autowired
    private InsightsService insightsService;

    @GetMapping("/insights")
    public String showInsights(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        var data = insightsService.getLongTermInsights(user);
        model.addAllAttributes(data);
        model.addAttribute("pageTitle", "Insights");
        model.addAttribute("topHeading", "Your Productivity Insights");
        return "insights";
    }
}
