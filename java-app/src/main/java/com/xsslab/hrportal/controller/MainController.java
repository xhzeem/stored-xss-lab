package com.xsslab.hrportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/health")
    @org.springframework.web.bind.annotation.ResponseBody
    public String health() {
        return "{\"status\":\"ok\"}";
    }
}
