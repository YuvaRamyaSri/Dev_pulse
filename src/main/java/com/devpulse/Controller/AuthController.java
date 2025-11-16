package com.devpulse.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.devpulse.Service.AuthService;
import com.devpulse.Service.DailyLogService;
import com.devpulse.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private DailyLogService dailyLogService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        User user = authService.validateUser(email, password);

        if (user != null) {
            session.setAttribute("loggedUser", user);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid email or password!");
            return "login";
        }
    }
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("recentLogs", dailyLogService.getLogsByUser(user));
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
