package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.TeamInvitation;
import com.klu.jobtracker.entity.TeamRole;

public record InvitationDetails(String organizationName, String email, TeamRole role) {
    public static InvitationDetails from(TeamInvitation invitation) {
        return new InvitationDetails(invitation.getOrganization().getName(),
                invitation.getEmail(), invitation.getRole());
    }
}
