package org.example.controller;

import org.example.model.Appointment;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class AppointmentController {

    private final VetService service;

    public AppointmentController(VetService service) {
        this.service = service;
    }

    // List all appointments
    @GetMapping("/appointments")
    public String appointmentsPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        model.addAttribute("appointments", service.getAllAppointments());
        return "appointments";
    }

    // CreateAppointmentSD - show form
    @GetMapping("/appointments/add")
    public String addAppointmentPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        model.addAttribute("animals", service.getAllAnimals());
        model.addAttribute("clients", service.getAllClients());
        model.addAttribute("veterinarians", service.getAllVeterinarians());
        return "add-appointment";
    }

    // CreateAppointmentSD - msg 7: clickSave()
    @PostMapping("/appointments/save")
    public String saveAppointment(
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String reason,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer veterinarianId,
            HttpSession session, Model model) {

        if (session.getAttribute("employee") == null) return "redirect:/login";

        boolean success = service.saveAppointment(LocalDate.parse(date), time, reason, animalId, clientId, veterinarianId);

        if (success) return "redirect:/appointments";

        model.addAttribute("error", "Could not save appointment.");
        model.addAttribute("animals", service.getAllAnimals());
        model.addAttribute("clients", service.getAllClients());
        model.addAttribute("veterinarians", service.getAllVeterinarians());
        return "add-appointment";
    }

    // UpdateAppointmentSD - show edit form
    @GetMapping("/appointments/edit/{id}")
    public String editAppointmentPage(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        Optional<Appointment> appt = service.getAppointmentById(id);
        if (appt.isEmpty()) return "redirect:/appointments";
        model.addAttribute("appointment", appt.get());
        model.addAttribute("animals", service.getAllAnimals());
        model.addAttribute("clients", service.getAllClients());
        model.addAttribute("veterinarians", service.getAllVeterinarians());
        return "edit-appointment";
    }

    // UpdateAppointmentSD - msg 7: clickSave()
    @PostMapping("/appointments/update")
    public String updateAppointment(
            @RequestParam int id,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String reason,
            @RequestParam(required = false) Integer animalId,
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer veterinarianId,
            HttpSession session, Model model) {

        if (session.getAttribute("employee") == null) return "redirect:/login";

        boolean success = service.updateAppointment(id, LocalDate.parse(date), time, reason, animalId, clientId, veterinarianId);

        if (success) return "redirect:/appointments";

        model.addAttribute("error", "Could not update appointment.");
        model.addAttribute("appointment", service.getAppointmentById(id).orElse(null));
        model.addAttribute("animals", service.getAllAnimals());
        model.addAttribute("clients", service.getAllClients());
        model.addAttribute("veterinarians", service.getAllVeterinarians());
        return "edit-appointment";
    }

    // CancelAppointmentSD
    @PostMapping("/appointments/cancel")
    public String cancelAppointment(@RequestParam int id, HttpSession session) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        service.cancelAppointment(id);
        return "redirect:/appointments";
    }
}