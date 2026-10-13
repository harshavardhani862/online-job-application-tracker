package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.ApplicationStatus;
import com.klu.jobtracker.entity.JobApplication;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.entity.Interview;
import com.klu.jobtracker.entity.InterviewStatus;
import com.klu.jobtracker.entity.Reminder;
import com.klu.jobtracker.repository.ApplicationRepository;
import com.klu.jobtracker.repository.UserRepository;
import com.klu.jobtracker.repository.InterviewRepository;
import com.klu.jobtracker.repository.ReminderRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.ComparisonTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads job-related messages from a dedicated Gmail inbox and creates/updates tracker rows. */
@Service
public class AutomaticStatusTrackingService {
    private static final Logger log = LoggerFactory.getLogger(AutomaticStatusTrackingService.class);
    private final ApplicationRepository applications;
    private final UserRepository users;
    private final InterviewRepository interviews;
    private final ReminderRepository reminders;

    @Value("${app.mail-tracking.enabled:false}") private boolean enabled;
    @Value("${app.mail-tracking.imap-host:imap.gmail.com}") private String host;
    @Value("${app.mail-tracking.imap-port:993}") private int port;
    @Value("${app.mail-tracking.username:}") private String username;
    @Value("${app.mail-tracking.password:}") private String password;
    @Value("${app.mail-tracking.owner-email:}") private String ownerEmail;

    public AutomaticStatusTrackingService(ApplicationRepository applications, UserRepository users,
                                          InterviewRepository interviews, ReminderRepository reminders) {
        this.applications = applications;
        this.users = users;
        this.interviews = interviews;
        this.reminders = reminders;
    }

    @Scheduled(fixedDelayString = "${app.mail-tracking.poll-interval-ms:60000}")
    public void scanInbox() {
        if (!enabled || blank(username) || blank(password)) return;
        String accountEmail = blank(ownerEmail) ? username : ownerEmail;
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", host);
            props.put("mail.imaps.port", String.valueOf(port));
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.connectiontimeout", "10000");
            props.put("mail.imaps.timeout", "15000");
            Session session = Session.getInstance(props);
            try (Store store = session.getStore("imaps")) {
                store.connect(host, port, username, password);
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);
                try {
                    // Scan recent mail, not only unread mail: users may open a confirmation before
                    // the next poll. Duplicate checks below make repeat scans safe.
                    Calendar cutoff = Calendar.getInstance();
                    cutoff.add(Calendar.DAY_OF_MONTH, -30);
                    Message[] recent = inbox.search(new ReceivedDateTerm(ComparisonTerm.GE, cutoff.getTime()));
                    for (Message message : recent) {
                        try {
                            String subject = Objects.toString(message.getSubject(), "");
                            String body = extractText(message);
                            String sender = "";
                            try {
                                Address[] from = message.getFrom();
                                if (from != null && from.length > 0) sender = from[0].toString();
                            } catch (Exception ignored) { }
                            // Include sender identity so status messages that omit the company
                            // name in their body can still match the correct application.
                            String allText = (subject + "\n" + sender + "\n" + body).toLowerCase(Locale.ROOT);
                            processMessage(accountEmail, message, subject, body, allText);
                            // Messages are not marked read or modified by the tracker.
                        } catch (Exception ex) {
                            log.warn("Could not process an inbox message; it will be retried: {}", ex.getMessage());
                        }
                    }
                } finally { inbox.close(false); }
            }
        } catch (Exception ex) {
            log.warn("Automatic email tracking could not connect to the inbox: {}", ex.getMessage());
        }
    }

    @Transactional
    protected void processMessage(String accountEmail, Message message, String subject, String body, String text) throws Exception {
        Optional<User> ownerOpt = users.findByEmail(accountEmail.trim());
        if (ownerOpt.isEmpty()) {
            log.warn("Mail tracking is enabled, but no Job Tracker user exists for configured owner email {}", accountEmail);
            return;
        }
        User owner = ownerOpt.get();
        ApplicationStatus detected = detectStatus(text);
        if (detected != null && updateMatchingApplication(owner, text, detected)
                && detected != ApplicationStatus.INTERVIEW) return;

        if (isApplicationConfirmation(text) || detected != null) {
            String title = extractJobTitle(subject, body);
            String company = extractCompany(message, body);
            if (blank(title) || blank(company)) {
                log.info("Job-related email detected but company/title could not be identified; not creating a possibly incorrect row. Subject: {}", subject);
                return;
            }
            String cleanTitle = cleanValue(title);
            String cleanCompany = cleanValue(company);
            if (cleanTitle.length() < 2 || cleanCompany.length() < 2) return;
            if (applications.findFirstByUserAndCompanyNameIgnoreCaseAndJobTitleIgnoreCase(owner, cleanCompany, cleanTitle).isPresent()) {
                log.debug("Duplicate application email ignored for {} / {}", cleanCompany, cleanTitle);
                return;
            }
            JobApplication app = new JobApplication();
            app.setUser(owner);
            app.setCompanyName(cleanCompany);
            app.setJobTitle(cleanTitle);
            try {
                Date received = message.getReceivedDate();
                app.setApplicationDate(received == null ? LocalDate.now() : received.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } catch (Exception ignored) { app.setApplicationDate(LocalDate.now()); }
            app.setStatus(detected == null ? ApplicationStatus.APPLIED : detected);
            app.setNotes("Automatically added from recruitment email. Subject: " + subject);
            app = applications.save(app);
            createFollowUpReminderIfMissing(app, "Follow up on job application",
                    LocalDateTime.now().plusDays(7),
                    "Automatically created from recruitment email.");
            log.info("Automatically added job application: {} / {}", cleanCompany, cleanTitle);
            if (detected == ApplicationStatus.INTERVIEW) createInterviewAndReminder(owner, app, body, subject);
        }
        // Interview invitations often arrive after the original confirmation. Match the existing
        // application, update status, and capture interview time/reminder when the email includes one.
        if (detected == ApplicationStatus.INTERVIEW) {
            JobApplication matched = findMatchingApplication(owner, text);
            if (matched != null) createInterviewAndReminder(owner, matched, body, subject);
        }
    }

    private boolean updateMatchingApplication(User owner, String text, ApplicationStatus status) {
        List<JobApplication> existing = applications.findByUserOrderByCreatedAtDesc(owner);
        for (JobApplication app : existing) {
            String company = normalize(app.getCompanyName());
            String title = normalize(app.getJobTitle());
            boolean companyFound = company.length() >= 3 && normalize(text).contains(company);
            boolean titleFound = title.length() >= 3 && normalize(text).contains(title);
            if (!companyFound) continue;
            // Subject/body sometimes omit the job title. Company match plus a clear status message is useful,
            // but avoid changing a row if the email explicitly names a different job title.
            if (!titleFound && containsDifferentJobTitle(text, app.getJobTitle())) continue;
            if (app.getStatus() == ApplicationStatus.REJECTED || app.getStatus() == ApplicationStatus.OFFER || app.getStatus() == ApplicationStatus.WITHDRAWN) continue;
            if (app.getStatus() != status) {
                app.setStatus(status);
                String oldNotes = app.getNotes() == null ? "" : app.getNotes().trim();
                app.setNotes((oldNotes.isEmpty() ? "" : oldNotes + "\n")
                        + "Status automatically updated to " + status
                        + " from recruitment email.");
                applications.save(app);
            }
            return true;
        }
        return false;
    }

    private JobApplication findMatchingApplication(User owner, String text) {
        String normalizedText = normalize(text);
        for (JobApplication app : applications.findByUserOrderByCreatedAtDesc(owner)) {
            String company = normalize(app.getCompanyName());
            String title = normalize(app.getJobTitle());
            if (company.length() >= 3 && normalizedText.contains(company)
                    && (title.length() < 3 || normalizedText.contains(title))) return app;
        }
        return null;
    }

    private void createInterviewAndReminder(User owner, JobApplication app, String body, String subject) {
        LocalDateTime interviewTime = extractInterviewDateTime(body + "\n" + subject);
        if (interviewTime == null) {
            // If the email asks the candidate to schedule a time but contains no confirmed slot,
            // create a gentle follow-up reminder rather than inventing an interview appointment.
            createFollowUpReminderIfMissing(app, "Follow up on interview scheduling",
                    LocalDateTime.now().plusDays(1), "Interview-related email received; confirm the interview date/time.");
            return;
        }
        List<Interview> existing = interviews.findByApplication_UserOrderByInterviewDateTimeAsc(owner);
        boolean exists = existing.stream().anyMatch(i -> i.getApplication().getId().equals(app.getId())
                && i.getInterviewDateTime() != null && Math.abs(ChronoUnit.MINUTES.between(i.getInterviewDateTime(), interviewTime)) <= 5);
        if (!exists) {
            Interview interview = new Interview();
            interview.setApplication(app);
            interview.setInterviewDateTime(interviewTime);
            interview.setStatus(InterviewStatus.SCHEDULED);
            interview.setInterviewType(inferInterviewType(body + " " + subject));
            interview.setNotes("Automatically extracted from recruitment email. Please verify the date, time and time zone.");
            interviews.save(interview);
        }
        LocalDateTime reminderTime = interviewTime.minusHours(24);
        if (reminderTime.isBefore(LocalDateTime.now().plusMinutes(5))) reminderTime = LocalDateTime.now().plusMinutes(5);
        createFollowUpReminderIfMissing(app, "Interview reminder: " + app.getCompanyName(), reminderTime,
                "Automatically created from interview email. Verify the interview time zone.");
    }

    private void createFollowUpReminderIfMissing(JobApplication app, String title, LocalDateTime when, String notes) {
        List<Reminder> existing = reminders.findByApplication_UserOrderByReminderDateTimeAsc(app.getUser());
        boolean exists = existing.stream().anyMatch(r -> r.getApplication().getId().equals(app.getId())
                && r.getTitle().equalsIgnoreCase(title));
        if (exists) return;
        Reminder reminder = new Reminder();
        reminder.setApplication(app);
        reminder.setTitle(title);
        reminder.setReminderDateTime(when);
        reminder.setNotes(notes);
        reminder.setCompleted(false);
        reminder.setNotificationSent(false);
        reminders.save(reminder);
    }

    private String inferInterviewType(String text) {
        String t = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (t.contains("video") || t.contains("zoom") || t.contains("google meet") || t.contains("teams meeting")) return "Video";
        if (t.contains("phone interview") || t.contains("telephone interview") || t.contains("call us")) return "Phone";
        if (t.contains("onsite") || t.contains("on-site") || t.contains("in person")) return "On-site";
        if (t.contains("technical interview")) return "Technical";
        return "Interview";
    }

    private LocalDateTime extractInterviewDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        // Conservative parsing for common email formats. If no reliable date/time is present,
        // no interview is invented; only a scheduling follow-up reminder is created.
        String[] patterns = {
            "(?i)(?:interview|meeting|appointment)(?: is)? (?:scheduled for|on|at)\\s+([A-Za-z]{3,9} \\d{1,2},? \\d{4}(?:,?\\s+at)?\\s+\\d{1,2}:\\d{2}\\s*(?:AM|PM)?)",
            "(?i)(?:date and time|interview date|scheduled for)\\s*[:\\-]?\\s*([A-Za-z]{3,9} \\d{1,2},? \\d{4}\\s+\\d{1,2}:\\d{2}\\s*(?:AM|PM))",
            "(?i)([A-Za-z]{3,9} \\d{1,2},? \\d{4}\\s+at\\s+\\d{1,2}(?::\\d{2})?\\s*(?:AM|PM))"
        };
        for (String regex : patterns) {
            Matcher m = Pattern.compile(regex).matcher(text);
            if (!m.find()) continue;
            String value = m.group(1).replaceAll("(?i)\\s+at\\s+", " ").replaceAll(",(?=\\s+\\d{1,2}:)", "").trim();
            String[] formats = {"MMMM d, yyyy h:mm a", "MMMM d yyyy h:mm a", "MMM d, yyyy h:mm a", "MMM d yyyy h:mm a", "MMMM d, yyyy H:mm", "MMM d, yyyy H:mm", "MMMM d yyyy h:mm", "MMM d yyyy h:mm"};
            for (String format : formats) {
                try { return LocalDateTime.parse(value, new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(format).toFormatter(Locale.ENGLISH)); }
                catch (DateTimeParseException ignored) { }
            }
        }
        return null;
    }

    private boolean containsDifferentJobTitle(String text, String currentTitle) {
        // Conservative guard: if a job-title phrase is present and doesn't match the row, don't update it.
        Matcher m = Pattern.compile("(?i)(?:position|role|job title|application for)\\s*[:\\-]?\\s*([^\\n\\r.;]{3,90})").matcher(text);
        while (m.find()) {
            String named = normalize(m.group(1));
            if (!named.isBlank() && !named.contains(normalize(currentTitle)) && !normalize(currentTitle).contains(named)) return true;
        }
        return false;
    }

    ApplicationStatus detectStatus(String text) {
        if (text == null || text.isBlank()) return null;
        String statusText = text.toLowerCase(Locale.ROOT);

        // Recruitment confirmations often contain conditional boilerplate such as
        // "If you are not selected, we will contact you". That is NOT a rejection.
        // Remove these conditional sentences before checking status keywords.
        statusText = statusText
                .replaceAll("(?is)if\\s+you\\s+are\\s+not\\s+selected[^.!?]*[.!?]", " ")
                .replaceAll("(?is)if\\s+you\\s+are\\s+unsuccessful[^.!?]*[.!?]", " ")
                .replaceAll("(?is)(?:only\\s+)?if\\s+(?:(?:you\\s+)?(?:are|have been)\\s+)?selected[^.!?]*[.!?]", " ")
                .replaceAll("(?is)only\\s+those\\s+candidates\\s+selected[^.!?]*[.!?]", " ");

        // Prefer explicit offer/interview/shortlist language. A generic mention of
        // "not selected" elsewhere in an email must not override an application receipt.
        if (has(statusText, "pleased to offer", "offer letter", "job offer", "we are offering you", "offer of employment")) return ApplicationStatus.OFFER;
        if (has(statusText, "interview invitation", "invite you to interview", "schedule an interview", "interview has been scheduled", "interview availability", "technical interview")) return ApplicationStatus.INTERVIEW;
        if (has(statusText, "shortlisted", "short-listed", "selected for the next round", "advance to the next round", "moved to the next stage")) return ApplicationStatus.SHORTLISTED;
        if (has(statusText, "not moving forward", "regret to inform", "unfortunately, we", "unfortunately we", "not selected for this role", "not selected for this position", "application was unsuccessful", "we will not be proceeding", "your application has been rejected", "we rejected your application", "your candidacy has been rejected")) return ApplicationStatus.REJECTED;
        if (has(statusText, "you have been selected", "you are selected",
                "selected for the position", "selected for this role", "we have selected you",
                "you have been selected to join", "you have been selected as",
                "you are selected for", "congratulations, you have been selected",
                "congratulations on being selected")) return ApplicationStatus.SELECTED;
        return null;
    }

    private boolean isApplicationConfirmation(String text) {
        boolean applicationWords = has(text, "application received", "successfully applied", "thank you for applying", "thanks for applying", "application confirmation", "your application has been submitted", "we have received your application", "application submitted", "received your application");
        boolean jobWords = has(text, "job", "career", "position", "role", "application");
        return applicationWords && jobWords;
    }

    private String extractJobTitle(String subject, String body) {
        List<String> sources = List.of(subject == null ? "" : subject, body == null ? "" : body);
        String[] patterns = {
            "(?i)(?:application received for|application for|applied for|position(?:\s+of)?|job title|role)\\s*[:\\-]?\\s*[\\\"']?([^\\n\\r<>\\\"']{3,100})",
            "(?i)thank you for applying(?: to)?(?: for)?\\s*[\\\"']?([^\\n\\r<>\\\"']{3,100})"
        };
        for (String source : sources) for (String regex : patterns) {
            Matcher m = Pattern.compile(regex).matcher(source);
            if (m.find()) {
                String candidate = cleanValue(m.group(1));
                candidate = candidate.replaceAll("(?i)\\s+(at|with)\\s+[^,;]{2,60}$", "").trim();
                if (candidate.length() >= 3 && candidate.length() <= 100 && !looksLikeGenericTitle(candidate)) return candidate;
            }
        }
        // Common subjects: "Thank you for applying - Software Engineer" or "Application received: Data Analyst".
        Matcher dash = Pattern.compile("(?i)(?:thank you for applying|application received|application submitted)\\s*[-:|–]\\s*([^\\n\\r]{3,100})").matcher(subject == null ? "" : subject);
        if (dash.find()) {
            String c = cleanValue(dash.group(1));
            if (!looksLikeGenericTitle(c)) return c;
        }
        return null;
    }

    private String extractCompany(Message message, String body) {
        try {
            Address[] from = message.getFrom();
            if (from != null && from.length > 0) {
                String personal = from[0] instanceof InternetAddress ia ? ia.getPersonal() : null;
                if (!blank(personal)) {
                    String cleaned = cleanCompany(personal);
                    if (!blank(cleaned) && !looksLikeGenericCompany(cleaned)) return cleaned;
                }
                String address = from[0].toString();
                Matcher em = Pattern.compile("(?i)[\\w.+-]+@([\\w.-]+)\\.[a-z]{2,}").matcher(address);
                if (em.find()) {
                    String domain = em.group(1).replaceFirst("(?i)^(mail|careers|jobs|notifications|noreply|no-reply|talent|recruiting|hr)\\.", "");
                    String first = domain.split("\\.")[0];
                    if (!isGenericDomain(first)) return titleCase(first.replace('-', ' ').replace('_', ' '));
                }
            }
        } catch (Exception ignored) { }
        Matcher m = Pattern.compile("(?i)(?:from|at|team)\\s+([A-Z][A-Za-z0-9& .'-]{1,50})(?:\\s+(?:careers|recruitment|talent|team))?").matcher(body == null ? "" : body);
        if (m.find()) {
            String candidate = cleanCompany(m.group(1));
            if (!looksLikeGenericCompany(candidate)) return candidate;
        }
        return null;
    }

    private String extractText(Part part) throws Exception {
        Object content = part.getContent();
        if (content instanceof String s) return s.replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>", " ").replaceAll("(?s)<[^>]+>", " ").replaceAll("&nbsp;", " ").replaceAll("&amp;", "&");
        if (content instanceof Multipart mp) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain") || bp.isMimeType("text/html") || bp.isMimeType("multipart/*")) result.append(extractText(bp)).append('\n');
            }
            return result.toString();
        }
        return "";
    }

    private boolean has(String text, String... phrases) { for (String phrase : phrases) if (text.contains(phrase)) return true; return false; }
    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String normalize(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " "); }
    private String cleanValue(String s) { return s == null ? null : s.replaceAll("(?is)<[^>]+>", " ").replaceAll("[\\t ]+", " ").replaceAll("[\\s\\-–|:;,]+$", "").trim(); }
    private String cleanCompany(String s) { return cleanValue(s == null ? null : s.replaceAll("(?i)\\b(careers|recruitment|talent acquisition|recruiting|notifications|noreply|no-reply)\\b", " ")); }
    private boolean looksLikeGenericTitle(String s) { return has(s.toLowerCase(Locale.ROOT), "your application", "application received", "thank you", "we received", "successfully submitted", "confirmation"); }
    private boolean looksLikeGenericCompany(String s) { return s == null || s.length() < 2 || isGenericDomain(s.toLowerCase(Locale.ROOT).replaceAll("\\s+", "")); }
    private boolean isGenericDomain(String s) { return Set.of("gmail", "yahoo", "outlook", "hotmail", "linkedin", "indeed", "naukri", "workday", "greenhouse", "lever", "ashby", "smartrecruiters", "icims", "myworkdayjobs", "amazonaws").contains(s.toLowerCase(Locale.ROOT)); }
    private String titleCase(String s) { StringBuilder b = new StringBuilder(); for (String p : s.split("\\s+")) { if (!b.isEmpty()) b.append(' '); if (!p.isEmpty()) b.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)); } return b.toString(); }
}
