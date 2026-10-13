package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.*;
import com.klu.jobtracker.repository.OrganizationRepository;
import com.klu.jobtracker.repository.TeamInvitationRepository;
import com.klu.jobtracker.repository.TeamMembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final TeamMembershipRepository membershipRepository;
    private final TeamInvitationRepository invitationRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                               TeamMembershipRepository membershipRepository,
                               TeamInvitationRepository invitationRepository) {
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.invitationRepository = invitationRepository;
    }

    @Transactional
    public void createWorkspace(User owner, String companyName) {
        String name = companyName == null || companyName.isBlank()
                ? owner.getName().trim() + "'s team"
                : companyName.trim();
        Organization organization = organizationRepository.save(new Organization(name, owner));
        membershipRepository.save(new TeamMembership(organization, owner, TeamRole.OWNER));
    }

    @Transactional
    public TeamMembership ensureWorkspace(User user) {
        return membershipRepository.findByUserId(user.getId()).orElseGet(() -> {
            Organization organization = organizationRepository.save(
                    new Organization(user.getName().trim() + "'s team", user));
            return membershipRepository.save(new TeamMembership(organization, user, TeamRole.OWNER));
        });
    }

    @Transactional
    public void acceptInvitation(User user, String token) {
        TeamInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("This team invitation is invalid or has expired."));
        if (invitation.getAcceptedAt() != null || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This team invitation is invalid or has expired.");
        }
        if (!invitation.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Create your account with the email address this invitation was sent to.");
        }
        if (membershipRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("This account already belongs to a workspace.");
        }
        membershipRepository.save(new TeamMembership(invitation.getOrganization(), user, invitation.getRole()));
        invitation.accept();
    }
}
