package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;

    private String jobUrl;
    private String location;
    private String salary;
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(length = 3000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public JobApplication() {}

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public String getJobUrl() { return jobUrl; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public ApplicationStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }
    public void setLocation(String location) { this.location = location; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUser(User user) { this.user = user; }
}
