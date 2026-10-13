package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.TeamMembership;
import com.klu.jobtracker.entity.TeamRole;
import java.time.LocalDateTime;

public record TeamMemberResponse(Long id, String name, String email, TeamRole role,
                                 LocalDateTime joinedAt) {
    public static TeamMemberResponse from(TeamMembership member) {
        return new TeamMemberResponse(member.getId(), member.getUser().getName(),
                member.getUser().getEmail(), member.getRole(), member.getJoinedAt());
    }
}
