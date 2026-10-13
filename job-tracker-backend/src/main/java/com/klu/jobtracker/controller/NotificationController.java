package com.klu.jobtracker.controller;

import com.klu.jobtracker.service.EmailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailNotificationService emailNotificationService;

    public NotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> sendTestEmail(Principal principal) {
        try {
            emailNotificationService.sendTestEmail(principal.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "Test email sent successfully",
                    "email", principal.getName()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", ex.getMessage() == null ? "Unable to send test email" : ex.getMessage()
            ));
        }
    }
}
