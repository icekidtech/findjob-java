package com.findjob.jobboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController - Handles landing page and general site pages
 */
@Controller
public class HomeController {
    
    /**
     * Display landing page
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }
    
    /**
     * Display about page (placeholder)
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About Us");
        return "about";
    }
    
    /**
     * Display FAQ page (placeholder)
     */
    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("title", "FAQ");
        return "faq";
    }
    
    /**
     * Display contact page (placeholder)
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact Us");
        return "contact";
    }
}
