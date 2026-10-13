package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.TeamRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TeamInvitationRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull TeamRole role
) {}
