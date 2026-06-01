package com.example.helpdesk.controller;


import java.util.List;
import com.example.helpdesk.entity.Ticket;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

	private String adminPassword =
	        "admin123";
    @Autowired
    private TicketRepository ticketRepository;
    
    // ================= HOME PAGE =================

    @GetMapping("/")
    public String home() {

        return "redirect:/login";
    }

    // ================= LOGIN PAGE =================

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }
    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        // Check Login
        if(session.getAttribute("admin") == null){

            return "redirect:/login";
        }

        List<Ticket> tickets =
                ticketRepository.findAll();

        long totalTickets =
                tickets.size();

        long resolvedTickets =
                tickets.stream()
                .filter(t ->
                        "Resolved".equalsIgnoreCase(
                                t.getStatus()))
                .count();

        long pendingTickets =
                tickets.stream()
                .filter(t ->
                        "Pending".equalsIgnoreCase(
                                t.getStatus()))
                .count();

        long highPriorityTickets =
                tickets.stream()
                .filter(t ->
                        "High".equalsIgnoreCase(
                                t.getPriority()))
                .count();

        long mediumPriorityTickets =
                tickets.stream()
                .filter(t ->
                        "Medium".equalsIgnoreCase(
                                t.getPriority()))
                .count();

        long lowPriorityTickets =
                tickets.stream()
                .filter(t ->
                        "Low".equalsIgnoreCase(
                                t.getPriority()))
                .count();

        model.addAttribute(
                "totalTickets",
                totalTickets);

        model.addAttribute(
                "resolvedTickets",
                resolvedTickets);

        model.addAttribute(
                "pendingTickets",
                pendingTickets);

        model.addAttribute(
                "highPriorityTickets",
                highPriorityTickets);

        model.addAttribute(
                "mediumPriorityTickets",
                mediumPriorityTickets);

        model.addAttribute(
                "lowPriorityTickets",
                lowPriorityTickets);

        model.addAttribute(
                "tickets",
                tickets);

        return "dashboard";
    }
  
    // ================= DASHBOARD =================

    @PostMapping("/login")
    public String login(

            @RequestParam String username,

            @RequestParam String password,

            HttpSession session,

            RedirectAttributes redirectAttributes){

        if(username.equals("Sahil@1234")
                &&password.equals(adminPassword)){

            session.setAttribute(
                    "admin",
                    "Sahil@1234");

            redirectAttributes.addFlashAttribute(
                    "success",
                    "✅ Login Successful");

            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute(
                "error",
                "❌ Invalid Username or Password");

        return "redirect:/login";
    }

    
    @GetMapping("/logout")
    public String logout(
            HttpSession session){

        session.invalidate();

        return "redirect:/login";
    }
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(){

        return "forgot-password";
    }
    @PostMapping("/reset-password")
    public String resetPassword(

            @RequestParam String username,

            @RequestParam String newPassword,

            RedirectAttributes redirectAttributes){

        if(username.equals("Sahil@1234")){

            adminPassword = newPassword;

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Password Updated Successfully");

            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute(
                "error",
                "Invalid Username");

        return "redirect:/forgot-password";
    }
}