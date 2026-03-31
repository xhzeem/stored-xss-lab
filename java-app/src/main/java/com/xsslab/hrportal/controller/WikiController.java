package com.xsslab.hrportal.controller;

import com.xsslab.hrportal.model.WikiPage;
import com.xsslab.hrportal.repository.WikiPageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WikiController {
    private final WikiPageRepository repo;

    public WikiController(WikiPageRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/wiki")
    public String list(Model model) {
        model.addAttribute("pages", repo.findAll());
        return "wiki";
    }

    @PostMapping("/wiki")
    public String create(@RequestParam String title,
                         @RequestParam String author,
                         @RequestParam String content) {
        repo.save(new WikiPage(title, author, content));
        return "redirect:/wiki";
    }
}
