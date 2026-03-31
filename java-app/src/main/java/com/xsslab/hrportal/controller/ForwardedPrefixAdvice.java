package com.xsslab.hrportal.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ForwardedPrefixAdvice {

    @ModelAttribute("prefix")
    public String getPrefix(HttpServletRequest request) {
        String prefix = request.getHeader("X-Forwarded-Prefix");
        return (prefix != null && !prefix.isEmpty()) ? prefix : "";
    }
}
