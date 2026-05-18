package org.example.controller;

import org.example.model.Employee;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final VetService service;

    public AuthController(VetService service) {
        this.service = service;
    }

    // LoginEmployeeSD - MainWindow: show()
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // LoginEmployeeSD - LoginWindow: show()
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // LoginEmployeeSD - msg 11: clickLogin()
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String email,       // msg 9: insertEmail()
            @RequestParam String password,    // msg 10: insertPassword()
            HttpSession session,
            Model model) {

        // LoginEmployeeSD - msg 12: employee = login(email, password)
        Employee employee = service.login(email, password);

        if (employee != null) {
            // [employee != null] - msg 18: DashboardWindow(s, employee)
            session.setAttribute("employee", employee);
            return "redirect:/dashboard";
        } else {
            // [employee == null] - msg 21: showAlert("Invalid credentials")
            model.addAttribute("error", "Invalid credentials. Please try again.");
            return "login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}