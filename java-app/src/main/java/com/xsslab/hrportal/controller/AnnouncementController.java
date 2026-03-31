package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.Announcement;
import com.xsslab.hrportal.repository.AnnouncementRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnnouncementController {
    private final AnnouncementRepository repo;

    public AnnouncementController(AnnouncementRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/announcements")
    public String list(Model model) {
        model.addAttribute("announcements", repo.findAll());
        return "announcements";
    }

    @PostMapping("/announcements")
    public String create(@RequestParam String author,
                         @RequestParam String title,
                         @RequestParam String content) {
        repo.save(new Announcement(author, title, content));
        return "redirect:/announcements";
    }
}
