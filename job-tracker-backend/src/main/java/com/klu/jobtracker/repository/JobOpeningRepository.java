package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.JobOpening;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    List<JobOpening> findAllByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
    Optional<JobOpening> findByIdAndOrganizationId(Long id, Long organizationId);
    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStatus(Long organizationId, com.klu.jobtracker.entity.OpeningStatus status);
}
