package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.ReminderRequest;
import com.klu.jobtracker.entity.*;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReminderService {
    private final ReminderRepository repository;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    public ReminderService(ReminderRepository repository, ApplicationRepository applicationRepository,
                           UserService userService) {
        this.repository = repository;
        this.applicationRepository = applicationRepository;
        this.userService = userService;
    }

    public List<Reminder> getAll(String email) {
        return repository.findByApplication_UserOrderByReminderDateTimeAsc(userService.getByEmail(email));
    }

    public Reminder getOne(String email, Long id) {
        User user = userService.getByEmail(email);
        return repository.findByIdAndApplication_User(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));
    }

    public Reminder create(String email, ReminderRequest r) {
        User user = userService.getByEmail(email);
        JobApplication app = applicationRepository.findByIdAndUser(r.getApplicationId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Reminder rem = new Reminder();
        rem.setApplication(app);
        copy(r, rem);
        return repository.save(rem);
    }

    public Reminder update(String email, Long id, ReminderRequest r) {
        Reminder rem = getOne(email, id);
        User user = userService.getByEmail(email);

        if (r.getApplicationId() != null) {
            JobApplication app = applicationRepository.findByIdAndUser(r.getApplicationId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
            rem.setApplication(app);
        }
        copy(r, rem);
        return repository.save(rem);
    }

    public void delete(String email, Long id) {
        repository.delete(getOne(email, id));
    }

    private void copy(ReminderRequest r, Reminder rem) {
        if (r.getTitle() != null) rem.setTitle(r.getTitle());
        if (r.getReminderDateTime() != null) {
            if (rem.getReminderDateTime() == null || !r.getReminderDateTime().equals(rem.getReminderDateTime())) {
                rem.setNotificationSent(false);
                rem.setNotificationSentAt(null);
            }
            rem.setReminderDateTime(r.getReminderDateTime());
        }
        rem.setCompleted(r.isCompleted());
        if (rem.isCompleted()) {
            rem.setNotificationSent(true);
        }
        rem.setNotes(r.getNotes());
    }
}
