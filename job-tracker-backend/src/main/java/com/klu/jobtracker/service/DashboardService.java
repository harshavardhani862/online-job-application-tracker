package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.ApplicationStatus;
import com.klu.jobtracker.entity.User;
import com.klu.jobtracker.repository.*;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final UserService userService;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final ReminderRepository reminderRepository;

    public DashboardService(UserService userService, ApplicationRepository applicationRepository,
                            InterviewRepository interviewRepository, ReminderRepository reminderRepository) {
        this.userService = userService;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.reminderRepository = reminderRepository;
    }

    public Map<String, Object> statistics(String email) {
        User user = userService.getByEmail(email);

        long total = applicationRepository.countByUser(user);
        long applied = applicationRepository.countByUserAndStatus(user, ApplicationStatus.APPLIED);
        long shortlisted = applicationRepository.countByUserAndStatus(user, ApplicationStatus.SHORTLISTED);
        long interview = applicationRepository.countByUserAndStatus(user, ApplicationStatus.INTERVIEW);
        long offers = applicationRepository.countByUserAndStatus(user, ApplicationStatus.OFFER);
        long rejected = applicationRepository.countByUserAndStatus(user, ApplicationStatus.REJECTED);

        double successRate = total == 0 ? 0.0 : (offers * 100.0 / total);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalApplications", total);
        result.put("applied", applied);
        result.put("shortlisted", shortlisted);
        // Names consumed by the React dashboard.
        result.put("interviews", interview);
        result.put("selected", offers);
        // Keep legacy aliases for existing pages that may use them.
        result.put("interview", interview);
        result.put("offers", offers);
        result.put("rejected", rejected);
        result.put("totalInterviews", interviewRepository.countByApplication_User(user));
        result.put("pendingReminders", reminderRepository.countByApplication_UserAndCompletedFalse(user));
        result.put("successRate", Math.round(successRate * 100.0) / 100.0);
        result.put("subscriptionStatus", user.getSubscriptionStatus().name());

        return result;
    }
}
