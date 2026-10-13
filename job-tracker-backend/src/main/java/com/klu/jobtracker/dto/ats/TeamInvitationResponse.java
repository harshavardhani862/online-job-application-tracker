package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.TeamInvitation;
import com.klu.jobtracker.entity.TeamRole;
import java.time.LocalDateTime;

public record TeamInvitationResponse(Long id, String email, TeamRole role, String token,
                                     LocalDateTime createdAt, LocalDateTime expiresAt) {
    public static TeamInvitationResponse from(TeamInvitation invitation) {
        return new TeamInvitationResponse(invitation.getId(), invitation.getEmail(),
                invitation.getRole(), invitation.getToken(), invitation.getCreatedAt(),
                invitation.getExpiresAt());
    }
}
