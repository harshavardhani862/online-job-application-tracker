package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.ats.CandidateRequest;
import com.klu.jobtracker.dto.ats.JobRequest;
import com.klu.jobtracker.entity.*;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitingServiceTenantTest {
    @Mock private UserRepository users;
    @Mock private TeamMembershipRepository memberships;
    @Mock private JobOpeningRepository jobs;
    @Mock private CandidateRepository candidates;
    @Mock private OrganizationService organizations;
    @Mock private TeamInvitationRepository invitations;
    @InjectMocks private RecruitingService service;

    @Test
    void cannotUpdateJobFromAnotherOrganization() {
        setupMember(11L);
        when(jobs.findByIdAndOrganizationId(90L, 11L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateJob(
                "recruiter@example.com", 90L,
                new JobRequest("Designer", "Design", "Remote", "Full-time", null, OpeningStatus.OPEN)));

        verify(jobs).findByIdAndOrganizationId(90L, 11L);
        verify(jobs, never()).save(any());
    }

    @Test
    void cannotAttachCandidateToJobFromAnotherOrganization() {
        setupMember(11L);
        when(jobs.findByIdAndOrganizationId(90L, 11L)).thenReturn(Optional.empty());
        CandidateRequest request = new CandidateRequest("Taylor Reed", "taylor@example.com", null,
                null, "Referral", null, null, 90L, CandidateStage.NEW);

        assertThrows(ResourceNotFoundException.class, () -> service.createCandidate(
                "recruiter@example.com", request));

        verify(jobs).findByIdAndOrganizationId(90L, 11L);
        verify(candidates, never()).save(any());
    }

    private void setupMember(Long organizationId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        Organization organization = mock(Organization.class);
        when(organization.getId()).thenReturn(organizationId);
        TeamMembership membership = mock(TeamMembership.class);
        when(membership.getUser()).thenReturn(user);
        when(membership.getOrganization()).thenReturn(organization);
        when(users.findByEmail("recruiter@example.com")).thenReturn(Optional.of(user));
        when(memberships.findByUserId(7L)).thenReturn(Optional.of(membership));
    }
}
