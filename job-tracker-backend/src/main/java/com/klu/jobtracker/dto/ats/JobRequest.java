package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.OpeningStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 80) String department,
        @NotBlank @Size(max = 120) String location,
        @NotBlank @Size(max = 40) String employmentType,
        @Size(max = 10000) String description,
        @NotNull OpeningStatus status
) {}
