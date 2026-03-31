package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.InterviewNote;
import com.xsslab.hrportal.repository.InterviewNoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InterviewController {
    private final InterviewNoteRepository repo;

    public InterviewController(InterviewNoteRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/interviews")
    public String list(Model model) {
        model.addAttribute("notes", repo.findAll());
        return "interviews";
    }

    @PostMapping("/interviews")
    public String create(@RequestParam int applicantId,
                         @RequestParam String interviewer,
                         @RequestParam String notes,
                         @RequestParam int rating) {
        repo.save(new InterviewNote(applicantId, interviewer, notes, rating));
        return "redirect:/interviews";
    }
}
