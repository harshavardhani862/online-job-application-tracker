package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.InterviewRequest;
import com.klu.jobtracker.entity.Interview;
import com.klu.jobtracker.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {
    private final InterviewService service;

    public InterviewController(InterviewService service) { this.service = service; }

    @GetMapping
    public List<Interview> getAll(Principal p) { return service.getAll(p.getName()); }

    @GetMapping("/{id}")
    public Interview getOne(Principal p, @PathVariable Long id) {
        return service.getOne(p.getName(), id);
    }

    @PostMapping
    public ResponseEntity<Interview> create(Principal p, @Valid @RequestBody InterviewRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(p.getName(), r));
    }

    @PutMapping("/{id}")
    public Interview update(Principal p, @PathVariable Long id, @Valid @RequestBody InterviewRequest r) {
        return service.update(p.getName(), id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal p, @PathVariable Long id) {
        service.delete(p.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Interview deleted"));
    }
}
