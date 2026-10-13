package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_openings", indexes = {
        @Index(name = "idx_job_openings_org", columnList = "organization_id")
})
public class JobOpening {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, length = 40)
    private String employmentType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OpeningStatus status = OpeningStatus.OPEN;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected JobOpening() {}

    public JobOpening(Organization organization, User createdBy, String title, String department,
                      String location, String employmentType, String description, OpeningStatus status) {
        this.organization = organization;
        this.createdBy = createdBy;
        this.title = title;
        this.department = department;
        this.location = location;
        this.employmentType = employmentType;
        this.description = description;
        this.status = status;
    }

    public void update(String title, String department, String location, String employmentType,
                       String description, OpeningStatus status) {
        this.title = title;
        this.department = department;
        this.location = location;
        this.employmentType = employmentType;
        this.description = description;
        this.status = status;
    }

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public User getCreatedBy() { return createdBy; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getLocation() { return location; }
    public String getEmploymentType() { return employmentType; }
    public String getDescription() { return description; }
    public OpeningStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
