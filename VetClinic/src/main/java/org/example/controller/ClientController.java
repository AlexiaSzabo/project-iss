package org.example.controller;

import org.example.model.Client;
import org.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ClientController {

    private final VetService service;

    public ClientController(VetService service) {
        this.service = service;
    }

    // RegisterClientSD - show list
    @GetMapping("/clients")
    public String clientsPage(HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        model.addAttribute("clients", service.getAllClients());
        return "clients";
    }

    // RegisterClientSD - show form
    @GetMapping("/clients/add")
    public String addClientPage(HttpSession session) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        return "add-client";
    }

    // RegisterClientSD - msg 9: saveClient()
    @PostMapping("/clients/save")
    public String saveClient(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        boolean success = service.saveClient(name, phone, email);
        if (success) return "redirect:/clients";
        model.addAttribute("error", "Could not save client. Email may already exist.");
        return "add-client";
    }

    // EditClientSD - show edit form
    @GetMapping("/clients/edit/{id}")
    public String editClientPage(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        Optional<Client> client = service.getClientById(id);
        if (client.isEmpty()) return "redirect:/clients";
        model.addAttribute("client", client.get());
        return "edit-client";
    }

    // EditClientSD - save changes
    @PostMapping("/clients/update")
    public String updateClient(
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            HttpSession session, Model model) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        boolean success = service.updateClient(id, name, phone, email);
        if (success) return "redirect:/clients";
        model.addAttribute("error", "Could not update client.");
        model.addAttribute("client", service.getClientById(id).orElse(null));
        return "edit-client";
    }

    // DeleteClientSD
    @PostMapping("/clients/delete")
    public String deleteClient(@RequestParam int id, HttpSession session) {
        if (session.getAttribute("employee") == null) return "redirect:/login";
        service.deleteClient(id);
        return "redirect:/clients";
    }
}