package csd.roster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.roster.service.interfaces.EmailService;

@RestController
public class EmailController {
    private EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @GetMapping("/email")
    public void sendMessage() {
        emailService.sendEmail("cs201newnormal@gmail.com", "CS201", "cs201newnormal@gmail.com", "test");
    }
}
