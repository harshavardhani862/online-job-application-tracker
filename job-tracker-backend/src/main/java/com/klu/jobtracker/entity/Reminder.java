
package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime reminderDateTime;

    private boolean completed = false;

    @Column(nullable = false)
    private boolean notificationSent = false;

    private LocalDateTime notificationSentAt;

    @Column(length = 1000)
    private String notes;

    public Reminder() {}

    public Long getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getReminderDateTime() {
        return reminderDateTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public LocalDateTime getNotificationSentAt() {
        return notificationSentAt;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReminderDateTime(LocalDateTime reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public void setNotificationSentAt(LocalDateTime notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
