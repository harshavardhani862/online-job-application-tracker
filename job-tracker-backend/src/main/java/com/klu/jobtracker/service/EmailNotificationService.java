package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.JobApplication;
import com.klu.jobtracker.entity.Reminder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public EmailNotificationService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean enabled,
            @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    public boolean isEnabled() {
        return enabled && from != null && !from.isBlank();
    }

    public void sendReminderEmail(Reminder reminder) {
        if (!isEnabled()) {
            throw new IllegalStateException(
                    "Email notifications are disabled. Set MAIL_ENABLED=true, MAIL_USERNAME and MAIL_PASSWORD.");
        }

        JobApplication application = reminder.getApplication();
        String recipient = application.getUser().getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Job Tracker Reminder: " + reminder.getTitle());

        String when = reminder.getReminderDateTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));

        String company = application.getCompanyName();
        String jobTitle = application.getJobTitle();

        message.setText(
                "Hello " + application.getUser().getName() + ",\n\n"
                + "This is your Online Job Tracker reminder.\n\n"
                + "Reminder: " + reminder.getTitle() + "\n"
                + "Company: " + company + "\n"
                + "Job Title: " + jobTitle + "\n"
                + "Reminder Time: " + when + "\n"
                + (reminder.getNotes() == null || reminder.getNotes().isBlank()
                    ? "" : "Notes: " + reminder.getNotes() + "\n")
                + "\nPlease follow up on this job application.\n\n"
                + "Regards,\nOnline Job Tracker"
        );

        mailSender.send(message);
    }

    public void sendTestEmail(String recipient) {
        if (!isEnabled()) {
            throw new IllegalStateException(
                    "Email notifications are disabled. Set MAIL_ENABLED=true, MAIL_USERNAME and MAIL_PASSWORD.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Online Job Tracker - Email Notification Test");
        message.setText(
                "Hello,\n\n"
                + "Your Online Job Tracker email notification is working successfully.\n\n"
                + "This is a test email from your Spring Boot application.\n\n"
                + "Regards,\nOnline Job Tracker"
        );

        mailSender.send(message);
    }
}
