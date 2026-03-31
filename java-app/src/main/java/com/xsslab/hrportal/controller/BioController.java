package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.EmployeeBio;
import com.xsslab.hrportal.repository.EmployeeBioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BioController {
    private final EmployeeBioRepository repo;

    public BioController(EmployeeBioRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/bios")
    public String list(Model model) {
        model.addAttribute("bios", repo.findAll());
        return "bios";
    }

    @PostMapping("/bios")
    public String create(@RequestParam String employeeName,
                         @RequestParam String department,
                         @RequestParam String bio,
                         @RequestParam String hobbies) {
        repo.save(new EmployeeBio(employeeName, department, bio, hobbies));
        return "redirect:/bios";
    }
}
