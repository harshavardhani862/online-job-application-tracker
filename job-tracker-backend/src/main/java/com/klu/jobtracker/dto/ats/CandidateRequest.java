package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.CandidateStage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CandidateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 40) String phone,
        @Size(max = 120) String location,
        @NotBlank @Size(max = 40) String source,
        @Size(max = 500) String resumeUrl,
        @Size(max = 10000) String notes,
        Long jobId,
        @NotNull CandidateStage stage
) {}
