package com.klu.jobtracker.dto.ats;

import java.util.List;

public record TeamResponse(String organizationName, List<TeamMemberResponse> members,
                           List<TeamInvitationResponse> pendingInvitations) {}
