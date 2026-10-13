package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.ApplicationRequest;
import com.klu.jobtracker.entity.JobApplication;
import com.klu.jobtracker.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;

    public ApplicationController(ApplicationService service) { this.service = service; }

    @GetMapping
    public List<JobApplication> getAll(Principal p) { return service.getAll(p.getName()); }

    @GetMapping("/{id}")
    public JobApplication getOne(Principal p, @PathVariable Long id) {
        return service.getOne(p.getName(), id);
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(Principal p, @Valid @RequestBody ApplicationRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(p.getName(), r));
    }

    @PostMapping("/auto-detected")
    public ResponseEntity<JobApplication> createAutoDetected(Principal p, @Valid @RequestBody ApplicationRequest r) {
        JobApplication saved = service.createAutoDetected(p.getName(), r);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public JobApplication update(Principal p, @PathVariable Long id, @Valid @RequestBody ApplicationRequest r) {
        return service.update(p.getName(), id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal p, @PathVariable Long id) {
        service.delete(p.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Application deleted"));
    }
}
