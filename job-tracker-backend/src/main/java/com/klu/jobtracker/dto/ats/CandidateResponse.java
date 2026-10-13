package com.klu.jobtracker.dto.ats;

import com.klu.jobtracker.entity.Candidate;
import com.klu.jobtracker.entity.CandidateStage;
import java.time.LocalDateTime;

public record CandidateResponse(Long id, String name, String email, String phone, String location,
                                String source, String resumeUrl, String notes, Long jobId,
                                String jobTitle, CandidateStage stage, LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
    public static CandidateResponse from(Candidate candidate) {
        return new CandidateResponse(candidate.getId(), candidate.getName(), candidate.getEmail(),
                candidate.getPhone(), candidate.getLocation(), candidate.getSource(),
                candidate.getResumeUrl(), candidate.getNotes(),
                candidate.getJobOpening() == null ? null : candidate.getJobOpening().getId(),
                candidate.getJobOpening() == null ? null : candidate.getJobOpening().getTitle(),
                candidate.getStage(), candidate.getCreatedAt(), candidate.getUpdatedAt());
    }
}
