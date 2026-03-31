package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.JobPosting;
import com.xsslab.hrportal.repository.JobPostingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobController {
    private final JobPostingRepository repo;

    public JobController(JobPostingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/jobs")
    public String list(Model model) {
        model.addAttribute("jobs", repo.findAll());
        return "jobs";
    }

    @PostMapping("/jobs")
    public String create(@RequestParam String title,
                         @RequestParam String department,
                         @RequestParam String description,
                         @RequestParam String requirements) {
        repo.save(new JobPosting(title, department, description, requirements));
        return "redirect:/jobs";
    }
}
