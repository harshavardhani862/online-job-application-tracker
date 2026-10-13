package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.Reminder;
import com.klu.jobtracker.repository.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderNotificationScheduler.class);

    private final ReminderRepository reminderRepository;
    private final EmailNotificationService emailNotificationService;

    public ReminderNotificationScheduler(
            ReminderRepository reminderRepository,
            EmailNotificationService emailNotificationService) {
        this.reminderRepository = reminderRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Scheduled(fixedDelayString = "${app.notification.scheduler-delay-ms:30000}")
    public void processDueReminders() {
        if (!emailNotificationService.isEnabled()) {
            return;
        }

        List<Reminder> dueReminders =
                reminderRepository
                    .findTop100ByReminderDateTimeLessThanEqualAndCompletedFalseAndNotificationSentFalseOrderByReminderDateTimeAsc(
                            LocalDateTime.now());

        for (Reminder reminder : dueReminders) {
            try {
                emailNotificationService.sendReminderEmail(reminder);
                reminder.setNotificationSent(true);
                reminder.setNotificationSentAt(LocalDateTime.now());
                reminderRepository.save(reminder);
                log.info("Reminder email sent for reminder id={} to={}",
                        reminder.getId(), reminder.getApplication().getUser().getEmail());
            } catch (Exception ex) {
                log.error("Failed to send reminder email for reminder id={}: {}",
                        reminder.getId(), ex.getMessage());
            }
        }
    }
}
