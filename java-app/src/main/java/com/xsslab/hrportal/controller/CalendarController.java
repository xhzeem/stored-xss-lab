package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.CalendarEvent;
import com.xsslab.hrportal.repository.CalendarEventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {
    private final CalendarEventRepository repo;

    public CalendarController(CalendarEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/calendar")
    public String list(Model model) {
        model.addAttribute("events", repo.findAll());
        return "calendar";
    }

    @PostMapping("/calendar")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam String eventDate,
                         @RequestParam String organizer) {
        repo.save(new CalendarEvent(title, description, eventDate, organizer));
        return "redirect:/calendar";
    }
}
