package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.ApplicationStatus;
import com.klu.jobtracker.entity.JobApplication;
import com.klu.jobtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserOrderByCreatedAtDesc(User user);
    Optional<JobApplication> findByIdAndUser(Long id, User user);
    long countByUser(User user);
    long countByUserAndStatus(User user, ApplicationStatus status);
    java.util.Optional<JobApplication> findFirstByUserAndCompanyNameIgnoreCaseAndJobTitleIgnoreCase(User user, String companyName, String jobTitle);
}
