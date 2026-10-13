package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.ats.*;
import com.klu.jobtracker.entity.*;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecruitingService {
    private final UserRepository userRepository;
    private final TeamMembershipRepository membershipRepository;
    private final JobOpeningRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final OrganizationService organizationService;
    private final TeamInvitationRepository invitationRepository;

    public RecruitingService(UserRepository userRepository, TeamMembershipRepository membershipRepository,
                             JobOpeningRepository jobRepository, CandidateRepository candidateRepository,
                             OrganizationService organizationService,
                             TeamInvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.organizationService = organizationService;
        this.invitationRepository = invitationRepository;
    }

    @Transactional
    public RecruitingOverview overview(String email) {
        TeamMembership member = member(email);
        Long organizationId = member.getOrganization().getId();
        List<JobResponse> jobs = jobRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId)
                .stream().map(JobResponse::from).toList();
        List<CandidateResponse> recent = candidateRepository.findTop6ByOrganizationIdOrderByUpdatedAtDesc(organizationId)
                .stream().map(CandidateResponse::from).toList();
        return new RecruitingOverview(member.getOrganization().getName(),
                jobRepository.countByOrganizationIdAndStatus(organizationId, OpeningStatus.OPEN),
                candidateRepository.countByOrganizationId(organizationId),
                candidateRepository.countByOrganizationIdAndStage(organizationId, CandidateStage.NEW),
                candidateRepository.countByOrganizationIdAndStage(organizationId, CandidateStage.SCREENING),
                candidateRepository.countByOrganizationIdAndStage(organizationId, CandidateStage.INTERVIEW),
                candidateRepository.countByOrganizationIdAndStage(organizationId, CandidateStage.OFFER),
                candidateRepository.countByOrganizationIdAndStage(organizationId, CandidateStage.HIRED),
                jobs, recent);
    }

    @Transactional
    public List<JobResponse> jobs(String email) {
        Long organizationId = member(email).getOrganization().getId();
        return jobRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId)
                .stream().map(JobResponse::from).toList();
    }

    @Transactional
    public JobResponse createJob(String email, JobRequest request) {
        TeamMembership member = member(email);
        JobOpening job = new JobOpening(member.getOrganization(), member.getUser(), request.title().trim(),
                request.department().trim(), request.location().trim(), request.employmentType().trim(),
                clean(request.description()), request.status());
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(String email, Long id, JobRequest request) {
        TeamMembership member = member(email);
        JobOpening job = job(id, member.getOrganization().getId());
        job.update(request.title().trim(), request.department().trim(), request.location().trim(),
                request.employmentType().trim(), clean(request.description()), request.status());
        return JobResponse.from(job);
    }

    @Transactional
    public void deleteJob(String email, Long id) {
        TeamMembership member = member(email);
        JobOpening job = job(id, member.getOrganization().getId());
        if (candidateRepository.existsByOrganizationIdAndJobOpeningId(
                member.getOrganization().getId(), id)) {
            throw new IllegalArgumentException("This opening still has candidates. Close it instead of deleting it.");
        }
        jobRepository.delete(job);
    }

    @Transactional
    public List<CandidateResponse> candidates(String email) {
        Long organizationId = member(email).getOrganization().getId();
        return candidateRepository.findAllByOrganizationIdOrderByUpdatedAtDesc(organizationId)
                .stream().map(CandidateResponse::from).toList();
    }

    @Transactional
    public CandidateResponse createCandidate(String email, CandidateRequest request) {
        TeamMembership member = member(email);
        JobOpening job = optionalJob(request.jobId(), member.getOrganization().getId());
        Candidate candidate = new Candidate(member.getOrganization(), job, member.getUser(),
                request.name().trim(), request.email().trim().toLowerCase(), clean(request.phone()),
                clean(request.location()), request.source().trim(), clean(request.resumeUrl()),
                clean(request.notes()), request.stage());
        return CandidateResponse.from(candidateRepository.save(candidate));
    }

    @Transactional
    public CandidateResponse updateCandidate(String email, Long id, CandidateRequest request) {
        TeamMembership member = member(email);
        Long organizationId = member.getOrganization().getId();
        Candidate candidate = candidate(id, organizationId);
        candidate.update(optionalJob(request.jobId(), organizationId), request.name().trim(),
                request.email().trim().toLowerCase(), clean(request.phone()), clean(request.location()),
                request.source().trim(), clean(request.resumeUrl()), clean(request.notes()), request.stage());
        return CandidateResponse.from(candidate);
    }

    @Transactional
    public void deleteCandidate(String email, Long id) {
        TeamMembership member = member(email);
        candidateRepository.delete(candidate(id, member.getOrganization().getId()));
    }

    @Transactional
    public TeamResponse team(String email) {
        TeamMembership current = member(email);
        Long organizationId = current.getOrganization().getId();
        List<TeamMemberResponse> members = membershipRepository
                .findAllByOrganizationIdOrderByJoinedAtAsc(organizationId)
                .stream().map(TeamMemberResponse::from).toList();
        List<TeamInvitationResponse> pending = invitationRepository
                .findAllByOrganizationIdAndAcceptedAtIsNullOrderByCreatedAtDesc(organizationId)
                .stream().filter(invitation -> invitation.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(TeamInvitationResponse::from).toList();
        return new TeamResponse(current.getOrganization().getName(), members, pending);
    }

    @Transactional
    public TeamInvitationResponse createTeamInvitation(String email, TeamInvitationRequest request) {
        TeamMembership current = member(email);
        if (current.getRole() != TeamRole.OWNER && current.getRole() != TeamRole.ADMIN) {
            throw new IllegalArgumentException("Only an owner or admin can invite team members.");
        }
        if (request.role() == TeamRole.OWNER) {
            throw new IllegalArgumentException("The owner role cannot be assigned to another member.");
        }
        String inviteEmail = request.email().trim().toLowerCase();
        if (userRepository.findByEmail(inviteEmail).isPresent()) {
            throw new IllegalArgumentException("This email already belongs to a TalentDesk account.");
        }
        if (membershipRepository.existsByOrganizationIdAndUserEmail(
                current.getOrganization().getId(), inviteEmail)) {
            throw new IllegalArgumentException("This person is already a member of the workspace.");
        }
        if (invitationRepository.existsByOrganizationIdAndEmailAndAcceptedAtIsNullAndExpiresAtAfter(
                current.getOrganization().getId(), inviteEmail, LocalDateTime.now())) {
            throw new IllegalArgumentException("There is already an active invitation for this email.");
        }
        TeamInvitation invitation = invitationRepository.save(new TeamInvitation(
                current.getOrganization(), current.getUser(), inviteEmail, request.role()));
        return TeamInvitationResponse.from(invitation);
    }

    @Transactional
    public InvitationDetails invitation(String token) {
        TeamInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Team invitation not found"));
        if (invitation.getAcceptedAt() != null || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This team invitation is invalid or has expired.");
        }
        return InvitationDetails.from(invitation);
    }

    private TeamMembership member(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return membershipRepository.findByUserId(user.getId())
                .orElseGet(() -> organizationService.ensureWorkspace(user));
    }

    private JobOpening job(Long id, Long organizationId) {
        return jobRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job opening not found"));
    }

    private Candidate candidate(Long id, Long organizationId) {
        return candidateRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }

    private JobOpening optionalJob(Long id, Long organizationId) {
        return id == null ? null : job(id, organizationId);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
