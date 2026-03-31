package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.Applicant;
import com.xsslab.hrportal.repository.ApplicantRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApplicantController {
    private final ApplicantRepository repo;

    public ApplicantController(ApplicantRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/applicants")
    public String list(Model model) {
        model.addAttribute("applicants", repo.findAll());
        return "applicants";
    }

    @GetMapping("/applicants/{id}")
    public String detail(@PathVariable int id, Model model) {
        Applicant applicant = repo.findById(id);
        model.addAttribute("applicant", applicant);
        return "applicant";
    }

    @PostMapping("/applicants")
    public String create(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String position,
                         @RequestParam String summary) {
        repo.save(new Applicant(name, email, position, summary, "new"));
        return "redirect:/applicants";
    }
}
