package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.ReminderRequest;
import com.klu.jobtracker.entity.Reminder;
import com.klu.jobtracker.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService service;

    public ReminderController(ReminderService service) {
        this.service = service;
    }

    // Return a flat JSON response instead of serializing lazy Hibernate entities.
    private Map<String, Object> toResponse(Reminder reminder) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", reminder.getId());
        response.put("applicationId", reminder.getApplication().getId());
        response.put("companyName", reminder.getApplication().getCompanyName());
        response.put("jobTitle", reminder.getApplication().getJobTitle());
        response.put("title", reminder.getTitle());
        response.put("reminderDateTime", reminder.getReminderDateTime());
        response.put("completed", reminder.isCompleted());
        response.put("notificationSent", reminder.isNotificationSent());
        response.put("notificationSentAt", reminder.getNotificationSentAt());
        response.put("notes", reminder.getNotes() == null ? "" : reminder.getNotes());
        return response;
    }

    @GetMapping
    public List<Map<String, Object>> getAll(Principal principal) {
        return service.getAll(principal.getName()).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOne(Principal principal, @PathVariable Long id) {
        return toResponse(service.getOne(principal.getName(), id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            Principal principal, @Valid @RequestBody ReminderRequest request) {
        Reminder saved = service.create(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(service.getOne(principal.getName(), saved.getId())));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(
            Principal principal, @PathVariable Long id, @Valid @RequestBody ReminderRequest request) {
        return toResponse(service.update(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(Principal principal, @PathVariable Long id) {
        service.delete(principal.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Reminder deleted"));
    }
}
