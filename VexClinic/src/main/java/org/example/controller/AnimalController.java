package org.example.controller;

import org.example.model.Animal;
import org.example.model.Employee;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AnimalController {

    private final VetService service;

    public AnimalController(VetService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) return "redirect:/login";
        var animals = service.getAllAnimals();
        model.addAttribute("employee", employee);
        model.addAttribute("animals", animals);
        model.addAttribute("animalCount", animals.size());
        model.addAttribute("appointmentCount", service.getAllAppointments().size());
        model.addAttribute("clientCount", service.getAllClients().size());
        return "dashboard";
    }

    // AddAnimalSD - show form
    @GetMapping("/animals/add")
    public String addAnimalPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        model.addAttribute("clients", service.getAllClients());
        return "add-animal";
    }

    // AddAnimalSD - msg 8: saveAnimal()
    @PostMapping("/animals/save")
    public String saveAnimal(
            @RequestParam String name,
            @RequestParam String species,
            @RequestParam(required = false) String breed,
            @RequestParam int age,
            @RequestParam(required = false) Integer clientId,
            HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        boolean success = service.saveAnimal(name, species, breed, age, clientId);
        if (success) return "redirect:/dashboard";
        model.addAttribute("error", "Could not save animal.");
        model.addAttribute("clients", service.getAllClients());
        return "add-animal";
    }

    // EditAnimalSD - show edit form
    @GetMapping("/animals/edit/{id}")
    public String editAnimalPage(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        Optional<Animal> animal = service.getAnimalById(id);
        if (animal.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("animal", animal.get());
        model.addAttribute("clients", service.getAllClients());
        return "edit-animal";
    }

    // EditAnimalSD - save changes
    @PostMapping("/animals/update")
    public String updateAnimal(
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam String species,
            @RequestParam(required = false) String breed,
            @RequestParam int age,
            @RequestParam(required = false) Integer clientId,
            HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        boolean success = service.updateAnimal(id, name, species, breed, age, clientId);
        if (success) return "redirect:/dashboard";
        model.addAttribute("error", "Could not update animal.");
        model.addAttribute("animal", service.getAnimalById(id).orElse(null));
        model.addAttribute("clients", service.getAllClients());
        return "edit-animal";
    }

    // DeleteAnimalSD
    @PostMapping("/animals/delete")
    public String deleteAnimal(@RequestParam int id, HttpSession session) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        service.deleteAnimal(id);
        return "redirect:/dashboard";
    }
}