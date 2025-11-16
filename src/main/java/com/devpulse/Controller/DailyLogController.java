package com.devpulse.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devpulse.model.DailyLog;
import com.devpulse.model.User;
import com.devpulse.Service.DailyLogService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/logs")
public class DailyLogController {

    @Autowired
    private DailyLogService dailyLogService;

    @GetMapping
    public String showLogs(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("logs", dailyLogService.getLogsByUser(user));
        return "daily-logs";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("dailyLog", new DailyLog());
        return "add-log";
    }

    @PostMapping("/add")
    public String addLog(@ModelAttribute DailyLog dailyLog, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        dailyLog.setUser(user);
        dailyLogService.saveLog(dailyLog);
        return "redirect:/logs";
    }
}
