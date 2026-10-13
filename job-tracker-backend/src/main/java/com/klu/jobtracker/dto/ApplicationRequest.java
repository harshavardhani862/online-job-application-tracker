package com.klu.jobtracker.dto;

import com.klu.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApplicationRequest {
    @NotBlank private String companyName;
    @NotBlank private String jobTitle;
    private String jobUrl;
    private String location;
    private String salary;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    private String notes;
    private LocalDateTime interviewDateTime;

    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public String getJobUrl() { return jobUrl; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public ApplicationStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public LocalDateTime getInterviewDateTime() { return interviewDateTime; }
    public void setCompanyName(String v) { companyName = v; }
    public void setJobTitle(String v) { jobTitle = v; }
    public void setJobUrl(String v) { jobUrl = v; }
    public void setLocation(String v) { location = v; }
    public void setSalary(String v) { salary = v; }
    public void setApplicationDate(LocalDate v) { applicationDate = v; }
    public void setStatus(ApplicationStatus v) { status = v; }
    public void setNotes(String v) { notes = v; }
    public void setInterviewDateTime(LocalDateTime v) { interviewDateTime = v; }
}
