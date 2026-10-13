package com.klu.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReminderRequest {
    @NotNull private Long applicationId;
    @NotBlank private String title;
    @NotNull private LocalDateTime reminderDateTime;
    private boolean completed;
    private String notes;

    public Long getApplicationId() { return applicationId; }
    public String getTitle() { return title; }
    public LocalDateTime getReminderDateTime() { return reminderDateTime; }
    public boolean isCompleted() { return completed; }
    public String getNotes() { return notes; }
    public void setApplicationId(Long v) { applicationId = v; }
    public void setTitle(String v) { title = v; }
    public void setReminderDateTime(LocalDateTime v) { reminderDateTime = v; }
    public void setCompleted(boolean v) { completed = v; }
    public void setNotes(String v) { notes = v; }
}
