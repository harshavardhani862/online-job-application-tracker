package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidates_org", columnList = "organization_id"),
        @Index(name = "idx_candidates_org_stage", columnList = "organization_id,stage")
})
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_opening_id")
    private JobOpening jobOpening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(length = 40)
    private String phone;

    @Column(length = 120)
    private String location;

    @Column(nullable = false, length = 40)
    private String source;

    @Column(length = 500)
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CandidateStage stage = CandidateStage.NEW;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Candidate() {}

    public Candidate(Organization organization, JobOpening jobOpening, User createdBy, String name,
                     String email, String phone, String location, String source, String resumeUrl,
                     String notes, CandidateStage stage) {
        this.organization = organization;
        this.jobOpening = jobOpening;
        this.createdBy = createdBy;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.source = source;
        this.resumeUrl = resumeUrl;
        this.notes = notes;
        this.stage = stage;
    }

    public void update(JobOpening jobOpening, String name, String email, String phone, String location,
                       String source, String resumeUrl, String notes, CandidateStage stage) {
        this.jobOpening = jobOpening;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.source = source;
        this.resumeUrl = resumeUrl;
        this.notes = notes;
        this.stage = stage;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public JobOpening getJobOpening() { return jobOpening; }
    public User getCreatedBy() { return createdBy; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getSource() { return source; }
    public String getResumeUrl() { return resumeUrl; }
    public String getNotes() { return notes; }
    public CandidateStage getStage() { return stage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
