package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.HrTicket;
import com.xsslab.hrportal.repository.HrTicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TicketController {
    private final HrTicketRepository repo;

    public TicketController(HrTicketRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/tickets")
    public String list(Model model) {
        model.addAttribute("tickets", repo.findAll());
        return "tickets";
    }

    @PostMapping("/tickets")
    public String create(@RequestParam String employeeName,
                         @RequestParam String subject,
                         @RequestParam String description) {
        repo.save(new HrTicket(employeeName, subject, description, "open"));
        return "redirect:/tickets";
    }
}
