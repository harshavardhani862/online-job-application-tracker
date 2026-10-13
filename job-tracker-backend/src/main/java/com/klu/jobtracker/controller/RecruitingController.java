package com.klu.jobtracker.controller;

import com.klu.jobtracker.dto.ats.*;
import com.klu.jobtracker.service.RecruitingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ats")
public class RecruitingController {
    private final RecruitingService service;

    public RecruitingController(RecruitingService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public RecruitingOverview overview(Principal principal) {
        return service.overview(principal.getName());
    }

    @GetMapping("/jobs")
    public List<JobResponse> jobs(Principal principal) {
        return service.jobs(principal.getName());
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(Principal principal, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createJob(principal.getName(), request));
    }

    @PutMapping("/jobs/{id}")
    public JobResponse updateJob(Principal principal, @PathVariable Long id,
                                 @Valid @RequestBody JobRequest request) {
        return service.updateJob(principal.getName(), id, request);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(Principal principal, @PathVariable Long id) {
        service.deleteJob(principal.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Job opening deleted"));
    }

    @GetMapping("/candidates")
    public List<CandidateResponse> candidates(Principal principal) {
        return service.candidates(principal.getName());
    }

    @PostMapping("/candidates")
    public ResponseEntity<CandidateResponse> createCandidate(
            Principal principal, @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCandidate(principal.getName(), request));
    }

    @PutMapping("/candidates/{id}")
    public CandidateResponse updateCandidate(Principal principal, @PathVariable Long id,
                                             @Valid @RequestBody CandidateRequest request) {
        return service.updateCandidate(principal.getName(), id, request);
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Map<String, String>> deleteCandidate(Principal principal, @PathVariable Long id) {
        service.deleteCandidate(principal.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted"));
    }

    @GetMapping("/team")
    public TeamResponse team(Principal principal) {
        return service.team(principal.getName());
    }

    @PostMapping("/team/invitations")
    public ResponseEntity<TeamInvitationResponse> inviteTeamMember(
            Principal principal, @Valid @RequestBody TeamInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createTeamInvitation(principal.getName(), request));
    }

    @GetMapping("/invitations/{token}")
    public InvitationDetails invitation(@PathVariable String token) {
        return service.invitation(token);
    }
}
