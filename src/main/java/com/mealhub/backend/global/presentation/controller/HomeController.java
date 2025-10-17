package com.mealhub.backend.global.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String redirectToSwagger() {
        return "redirect:/api-docs.html";
    }
}
