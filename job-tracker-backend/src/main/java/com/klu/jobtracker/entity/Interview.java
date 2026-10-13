
package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    @Column(nullable = false)
    private LocalDateTime interviewDateTime;

    private String interviewerName;
    private String interviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Column(length = 2000)
    private String notes;

    public Interview() {}

    public Long getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
