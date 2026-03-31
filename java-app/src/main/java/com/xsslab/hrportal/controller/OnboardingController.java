package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.OnboardingTask;
import com.xsslab.hrportal.repository.OnboardingTaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OnboardingController {
    private final OnboardingTaskRepository repo;

    public OnboardingController(OnboardingTaskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/onboarding")
    public String list(Model model) {
        model.addAttribute("tasks", repo.findAll());
        return "onboarding";
    }

    @PostMapping("/onboarding")
    public String create(@RequestParam String employeeName,
                         @RequestParam String taskName,
                         @RequestParam String notes,
                         @RequestParam(defaultValue = "false") boolean completed) {
        repo.save(new OnboardingTask(employeeName, taskName, notes, completed));
        return "redirect:/onboarding";
    }
}
