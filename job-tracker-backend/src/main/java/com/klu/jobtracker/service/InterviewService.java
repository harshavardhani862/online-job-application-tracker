package com.klu.jobtracker.service;

import com.klu.jobtracker.dto.InterviewRequest;
import com.klu.jobtracker.entity.*;
import com.klu.jobtracker.exception.ResourceNotFoundException;
import com.klu.jobtracker.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InterviewService {
    private final InterviewRepository repository;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    public InterviewService(InterviewRepository repository, ApplicationRepository applicationRepository,
                            UserService userService) {
        this.repository = repository;
        this.applicationRepository = applicationRepository;
        this.userService = userService;
    }

    public List<Interview> getAll(String email) {
        return repository.findByApplication_UserOrderByInterviewDateTimeAsc(userService.getByEmail(email));
    }

    public Interview getOne(String email, Long id) {
        User user = userService.getByEmail(email);
        return repository.findByIdAndApplication_User(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
    }

    public Interview create(String email, InterviewRequest r) {
        User user = userService.getByEmail(email);
        JobApplication app = applicationRepository.findByIdAndUser(r.getApplicationId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Interview i = new Interview();
        i.setApplication(app);
        copy(r, i);
        return repository.save(i);
    }

    public Interview update(String email, Long id, InterviewRequest r) {
        Interview i = getOne(email, id);
        User user = userService.getByEmail(email);

        if (r.getApplicationId() != null) {
            JobApplication app = applicationRepository.findByIdAndUser(r.getApplicationId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
            i.setApplication(app);
        }
        copy(r, i);
        return repository.save(i);
    }

    public void delete(String email, Long id) {
        repository.delete(getOne(email, id));
    }

    private void copy(InterviewRequest r, Interview i) {
        if (r.getInterviewDateTime() != null) i.setInterviewDateTime(r.getInterviewDateTime());
        i.setInterviewerName(r.getInterviewerName());
        i.setInterviewType(r.getInterviewType());
        if (r.getStatus() != null) i.setStatus(r.getStatus());
        i.setNotes(r.getNotes());
    }
}
