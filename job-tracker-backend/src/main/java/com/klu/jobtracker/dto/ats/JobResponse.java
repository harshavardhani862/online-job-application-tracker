package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.JobOpening;
import com.klu.jobtracker.entity.OpeningStatus;
import java.time.LocalDateTime;

public record JobResponse(Long id, String title, String department, String location,
                          String employmentType, String description, OpeningStatus status,
                          LocalDateTime createdAt) {
    public static JobResponse from(JobOpening job) {
        return new JobResponse(job.getId(), job.getTitle(), job.getDepartment(), job.getLocation(),
                job.getEmploymentType(), job.getDescription(), job.getStatus(), job.getCreatedAt());
    }
}
