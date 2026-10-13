package com.klu.jobtracker.controller;

import com.klu.jobtracker.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/statistics")
    public Map<String, Object> statistics(Principal p) {
        return service.statistics(p.getName());
    }
}
