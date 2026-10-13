package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.ApplicationRequest;
import com.klu.jobtracker.entity.ApplicationStatus;
import com.klu.jobtracker.entity.Interview;
import com.klu.jobtracker.entity.InterviewStatus;
import com.klu.jobtracker.entity.JobApplication;
import com.klu.jobtracker.entity.Reminder;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.ApplicationRepository;
import com.klu.jobtracker.repository.InterviewRepository;
import com.klu.jobtracker.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository repository;
    private final UserService userService;
    private final InterviewRepository interviewRepository;
    private final ReminderRepository reminderRepository;

    public ApplicationService(
            ApplicationRepository repository,
            UserService userService,
            InterviewRepository interviewRepository,
            ReminderRepository reminderRepository) {
        this.repository = repository;
        this.userService = userService;
        this.interviewRepository = interviewRepository;
        this.reminderRepository = reminderRepository;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getAll(String email) {
        return repository.findByUserOrderByCreatedAtDesc(
                userService.getByEmail(email));
    }

    @Transactional(readOnly = true)
    public JobApplication getOne(String email, Long id) {
        User user = userService.getByEmail(email);
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found"));
    }

    @Transactional
    public JobApplication create(String email, ApplicationRequest request) {
        User user = userService.getByEmail(email);
        String company = requireText(request.getCompanyName(), "Company name");
        String title = requireText(request.getJobTitle(), "Job title");

        // Prevent accidental duplicate rows when a user adds an application manually.
        JobApplication existing = repository
                .findFirstByUserAndCompanyNameIgnoreCaseAndJobTitleIgnoreCase(
                        user, company, title)
                .orElse(null);
        if (existing != null) {
            return existing;
        }

        JobApplication application = new JobApplication();
        application.setUser(user);
        copy(request, application);
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(LocalDate.now());
        }
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.APPLIED);
        }
        return repository.save(application);
    }

    /**
     * Create or update a row detected from Gmail or the browser extension.
     * Repeated scans do not create duplicate application rows. An old APPLIED
     * confirmation never changes an existing later status back to APPLIED.
     */
    @Transactional
    public JobApplication createAutoDetected(
            String email, ApplicationRequest request) {

        User user = userService.getByEmail(email);
        String company = requireText(request.getCompanyName(), "Company name");
        String title = requireText(request.getJobTitle(), "Job title");

        JobApplication application = repository
                .findFirstByUserAndCompanyNameIgnoreCaseAndJobTitleIgnoreCase(
                        user, company, title)
                .orElse(null);

        if (application == null) {
            application = new JobApplication();
            application.setUser(user);
            application.setCompanyName(company);
            application.setJobTitle(title);
            copyOptionalFields(request, application);
            application.setApplicationDate(request.getApplicationDate() != null
                    ? request.getApplicationDate() : LocalDate.now());
            application.setStatus(request.getStatus() != null
                    ? request.getStatus() : ApplicationStatus.APPLIED);
            application.setNotes(appendNote(
                    application.getNotes(),
                    "Automatically detected by Job Tracker. Please verify details."));
            application = repository.save(application);
        } else {
            // Add newly discovered information without erasing existing values.
            copyMissingFields(request, application);

            ApplicationStatus incoming = request.getStatus();
            if (incoming != null && incoming != ApplicationStatus.APPLIED) {
                // A later status from an email/extension may update the row.
                // Do not overwrite terminal outcomes with an ordinary interview update.
                ApplicationStatus current = application.getStatus();
                boolean terminal = current == ApplicationStatus.REJECTED
                        || current == ApplicationStatus.OFFER
                        || current == ApplicationStatus.WITHDRAWN;
                boolean incomingTerminal = incoming == ApplicationStatus.REJECTED
                        || incoming == ApplicationStatus.OFFER
                        || incoming == ApplicationStatus.WITHDRAWN;
                if (!terminal || incomingTerminal) {
                    application.setStatus(incoming);
                }
            }
            application = repository.save(application);
        }

        if (request.getStatus() == ApplicationStatus.INTERVIEW) {
            recordDetectedInterview(
                    application, request.getInterviewDateTime());
        }
        return application;
    }

    private void recordDetectedInterview(
            JobApplication application, LocalDateTime interviewTime) {

        User user = application.getUser();
        List<Interview> existingInterviews = interviewRepository
                .findByApplication_UserOrderByInterviewDateTimeAsc(user);

        if (interviewTime == null) {
            createAutoReminderIfMissing(
                    application,
                    "Follow up on interview scheduling",
                    LocalDateTime.now().plusDays(1),
                    "An interview-related email/page was detected, but no reliable "
                            + "date and time could be read. Please verify manually.");
            return;
        }

        boolean duplicateInterview = existingInterviews.stream().anyMatch(interview ->
                interview.getApplication() != null
                        && interview.getApplication().getId().equals(application.getId())
                        && interview.getInterviewDateTime() != null
                        && Math.abs(ChronoUnit.MINUTES.between(
                                interview.getInterviewDateTime(), interviewTime)) <= 5);

        if (!duplicateInterview) {
            Interview interview = new Interview();
            interview.setApplication(application);
            interview.setInterviewDateTime(interviewTime);
            interview.setStatus(InterviewStatus.SCHEDULED);
            interview.setInterviewType("Interview detected from email or job website");
            interview.setNotes("Automatically detected by Job Tracker. "
                    + "Please verify the date, time and time zone.");
            interviewRepository.save(interview);
        }

        LocalDateTime reminderAt = interviewTime.minusHours(24);
        if (!reminderAt.isAfter(LocalDateTime.now())) {
            reminderAt = LocalDateTime.now().plusMinutes(5);
        }
        createAutoReminderIfMissing(
                application,
                "Interview reminder: " + application.getCompanyName(),
                reminderAt,
                "Automatically created for a detected interview. Please verify the details.");
    }

    private void createAutoReminderIfMissing(
            JobApplication application,
            String title,
            LocalDateTime reminderTime,
            String notes) {

        List<Reminder> existing = reminderRepository
                .findByApplication_UserOrderByReminderDateTimeAsc(
                        application.getUser());

        boolean duplicate = existing.stream().anyMatch(reminder ->
                reminder.getApplication() != null
                        && reminder.getApplication().getId().equals(application.getId())
                        && reminder.getTitle() != null
                        && reminder.getTitle().equalsIgnoreCase(title));
        if (duplicate) {
            return;
        }

        Reminder reminder = new Reminder();
        reminder.setApplication(application);
        reminder.setTitle(title);
        reminder.setReminderDateTime(reminderTime);
        reminder.setNotes(notes);
        reminder.setCompleted(false);
        reminder.setNotificationSent(false);
        reminderRepository.save(reminder);
    }

    @Transactional
    public JobApplication update(
            String email, Long id, ApplicationRequest request) {
        JobApplication application = getOne(email, id);
        copy(request, application);
        return repository.save(application);
    }

    /** Delete child records first to avoid foreign-key constraint errors. */
    @Transactional
    public void delete(String email, Long id) {
        JobApplication application = getOne(email, id);
        int remindersDeleted = reminderRepository.deleteAllForApplication(id);
        int interviewsDeleted = interviewRepository.deleteAllForApplication(id);
        repository.delete(application);
        repository.flush();
    }

    private void copy(ApplicationRequest request, JobApplication application) {
        application.setCompanyName(requireText(request.getCompanyName(), "Company name"));
        application.setJobTitle(requireText(request.getJobTitle(), "Job title"));
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        application.setSalary(request.getSalary());
        if (request.getApplicationDate() != null) {
            application.setApplicationDate(request.getApplicationDate());
        }
        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }
        application.setNotes(request.getNotes());
    }

    private void copyOptionalFields(
            ApplicationRequest request, JobApplication application) {
        if (request.getJobUrl() != null && !request.getJobUrl().isBlank()) {
            application.setJobUrl(request.getJobUrl().trim());
        }
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            application.setLocation(request.getLocation().trim());
        }
        if (request.getSalary() != null && !request.getSalary().isBlank()) {
            application.setSalary(request.getSalary().trim());
        }
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            application.setNotes(request.getNotes().trim());
        }
    }

    private void copyMissingFields(
            ApplicationRequest request, JobApplication application) {
        if (isBlank(application.getJobUrl()) && !isBlank(request.getJobUrl())) {
            application.setJobUrl(request.getJobUrl().trim());
        }
        if (isBlank(application.getLocation()) && !isBlank(request.getLocation())) {
            application.setLocation(request.getLocation().trim());
        }
        if (isBlank(application.getSalary()) && !isBlank(request.getSalary())) {
            application.setSalary(request.getSalary().trim());
        }
        if (application.getApplicationDate() == null
                && request.getApplicationDate() != null) {
            application.setApplicationDate(request.getApplicationDate());
        }
        if (!isBlank(request.getNotes()) && isBlank(application.getNotes())) {
            application.setNotes(request.getNotes().trim());
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String appendNote(String existing, String addition) {
        if (isBlank(existing)) {
            return addition;
        }
        if (existing.contains(addition)) {
            return existing;
        }
        return existing + "\n" + addition;
    }
}
