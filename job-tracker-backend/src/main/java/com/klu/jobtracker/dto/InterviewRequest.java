package com.klu.jobtracker.dto;

import com.klu.jobtracker.entity.InterviewStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InterviewRequest {
    @NotNull private Long applicationId;
    @NotNull private LocalDateTime interviewDateTime;
    private String interviewerName;
    private String interviewType;
    private InterviewStatus status;
    private String notes;

    public Long getApplicationId() { return applicationId; }
    public LocalDateTime getInterviewDateTime() { return interviewDateTime; }
    public String getInterviewerName() { return interviewerName; }
    public String getInterviewType() { return interviewType; }
    public InterviewStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public void setApplicationId(Long v) { applicationId = v; }
    public void setInterviewDateTime(LocalDateTime v) { interviewDateTime = v; }
    public void setInterviewerName(String v) { interviewerName = v; }
    public void setInterviewType(String v) { interviewType = v; }
    public void setStatus(InterviewStatus v) { status = v; }
    public void setNotes(String v) { notes = v; }
}
