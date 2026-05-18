package org.example.controller;

import org.example.model.Employee;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final VetService service;

    public AdminController(VetService service) {
        this.service = service;
    }

    // ── helper: only ADMIN can access ──────────────
    private boolean isAdmin(HttpSession session) {
        Employee emp = (Employee) session.getAttribute("employee");
        return emp != null && "ADMIN".equalsIgnoreCase(emp.getRole());
    }

    // ── VETERINARIANS ──────────────────────────────

    // ManageVeterinariansSD - show list
    @GetMapping("/veterinarians")
    public String veterinariansPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        model.addAttribute("veterinarians", service.getAllVeterinarians());
        return "veterinarians";
    }

    // ManageVeterinariansSD - show add form
    @GetMapping("/veterinarians/add")
    public String addVeterinarianPage(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/veterinarians";
        return "add-veterinarian";
    }

    // ManageVeterinariansSD - msg 7: saveVeterinarian()
    @PostMapping("/veterinarians/save")
    public String saveVeterinarian(
            @RequestParam String name,
            @RequestParam String specialization,
            @RequestParam String phone,
            @RequestParam String email,
            HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/veterinarians";
        boolean success = service.saveVeterinarian(name, specialization, phone, email);
        if (success) return "redirect:/veterinarians";
        model.addAttribute("error", "Could not save. Email or phone may already exist.");
        return "add-veterinarian";
    }

    // ManageVeterinariansSD - show edit form
    @GetMapping("/veterinarians/edit/{id}")
    public String editVeterinarianPage(@PathVariable int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/veterinarians";
        var vet = service.getVeterinarianById(id);
        if (vet.isEmpty()) return "redirect:/veterinarians";
        model.addAttribute("vet", vet.get());
        return "edit-veterinarian";
    }

    // ManageVeterinariansSD - msg 8: updateVeterinarian()
    @PostMapping("/veterinarians/update")
    public String updateVeterinarian(
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam String specialization,
            @RequestParam String phone,
            @RequestParam String email,
            HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/veterinarians";
        boolean success = service.updateVeterinarian(id, name, specialization, phone, email);
        if (success) return "redirect:/veterinarians";
        model.addAttribute("error", "Could not update.");
        model.addAttribute("vet", service.getVeterinarianById(id).orElse(null));
        return "edit-veterinarian";
    }

    // ManageVeterinariansSD - deleteVeterinarian()
    @PostMapping("/veterinarians/delete")
    public String deleteVeterinarian(@RequestParam int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/veterinarians";
        service.deleteVeterinarian(id);
        return "redirect:/veterinarians";
    }

    // ── USERS ──────────────────────────────────────

    // ManageUsersSD - show list
    @GetMapping("/users")
    public String usersPage(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        model.addAttribute("users", service.getAllEmployees());
        return "users";
    }

    // ManageUsersSD - show add form
    @GetMapping("/users/add")
    public String addUserPage(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        return "add-user";
    }

    // ManageUsersSD - msg 7: saveEmployee()
    @PostMapping("/users/save")
    public String saveUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        boolean success = service.saveEmployee(name, email, password, role);
        if (success) return "redirect:/users";
        model.addAttribute("error", "Could not save. Email may already exist.");
        return "add-user";
    }

    // ManageUsersSD - show edit form
    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        var user = service.getEmployeeById(id);
        if (user.isEmpty()) return "redirect:/users";
        model.addAttribute("user", user.get());
        return "edit-user";
    }

    // ManageUsersSD - msg 8: updateEmployee()
    @PostMapping("/users/update")
    public String updateUser(
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam String role,
            HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        boolean success = service.updateEmployee(id, name, email, password, role);
        if (success) return "redirect:/users";
        model.addAttribute("error", "Could not update.");
        model.addAttribute("user", service.getEmployeeById(id).orElse(null));
        return "edit-user";
    }

    // ManageUsersSD - deleteEmployee()
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/dashboard";
        service.deleteEmployee(id);
        return "redirect:/users";
    }

    // ── REPORTS ────────────────────────────────────

    // GenerateReportsSD - show reports page
    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        var data = service.getReportData();
        data.forEach(model::addAttribute);
        return "reports";
    }
}