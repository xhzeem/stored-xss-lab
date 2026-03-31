package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.AuditLog;
import com.xsslab.hrportal.repository.AuditLogRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuditController {
    private final AuditLogRepository repo;

    public AuditController(AuditLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/audit")
    public String list(Model model) {
        model.addAttribute("logs", repo.findAll());
        return "audit";
    }

    @PostMapping("/audit")
    public String create(@RequestParam String actor,
                         @RequestParam String action,
                         @RequestParam String details) {
        repo.save(new AuditLog(actor, action, details));
        return "redirect:/audit";
    }
}
