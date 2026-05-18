package org.example.controller;

import org.example.model.Consultation;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ConsultationController {

    private final VetService service;

    public ConsultationController(VetService service) {
        this.service = service;
    }

    // RecordConsultationSD - show form (se ajunge din appointments cu butonul "Record Consultation")
    @GetMapping("/consultations/add")
    public String addConsultationPage(@RequestParam int appointmentId, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        var appt = service.getAppointmentById(appointmentId);
        if (appt.isEmpty()) return "redirect:/appointments";
        model.addAttribute("appointment", appt.get());
        return "add-consultation";
    }

    // RecordConsultationSD - msg 8: clickSave()
    @PostMapping("/consultations/save")
    public String saveConsultation(
            @RequestParam int appointmentId,
            @RequestParam String symptoms,
            @RequestParam String diagnosis,
            @RequestParam(required = false) String recommendations,
            @RequestParam double cost,
            HttpSession session, Model model) {

        if (session.getAttribute("employee") == null) return "redirect:/login";

        boolean success = service.saveConsultation(appointmentId, symptoms, diagnosis, recommendations, cost);

        if (success) return "redirect:/appointments";

        model.addAttribute("error", "Could not save consultation.");
        model.addAttribute("appointment", service.getAppointmentById(appointmentId).orElse(null));
        return "add-consultation";
    }

    // ViewMedicalHistorySD - show history for an animal
    @GetMapping("/consultations/history")
    public String medicalHistory(@RequestParam int animalId, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        var animal = service.getAnimalById(animalId);
        if (animal.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("animal", animal.get());
        model.addAttribute("consultations", service.getMedicalHistory(animalId));
        return "medical-history";
    }

    // AddTreatmentSD - show form
    @GetMapping("/treatments/add")
    public String addTreatmentPage(@RequestParam int consultationId, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        var cons = service.getConsultationById(consultationId);
        if (cons.isEmpty()) return "redirect:/appointments";
        model.addAttribute("consultation", cons.get());
        return "add-treatment";
    }

    // AddTreatmentSD - msg 8: saveTreatment()
    @PostMapping("/treatments/save")
    public String saveTreatment(
            @RequestParam int consultationId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String dosage,
            @RequestParam(required = false) String duration,
            HttpSession session, Model model) {

        if (session.getAttribute("employee") == null) return "redirect:/login";

        boolean success = service.saveTreatment(consultationId, name, description, dosage, duration);

        if (success) {
            // Redirect to history of the animal
            Optional<Consultation> cons = service.getConsultationById(consultationId);
            int animalId = cons.map(c -> c.getAnimal() != null ? c.getAnimal().getId() : 0).orElse(0);
            if (animalId > 0) return "redirect:/consultations/history?animalId=" + animalId;
            return "redirect:/appointments";
        }

        model.addAttribute("error", "Could not save treatment.");
        model.addAttribute("consultation", service.getConsultationById(consultationId).orElse(null));
        return "add-treatment";
    }
}