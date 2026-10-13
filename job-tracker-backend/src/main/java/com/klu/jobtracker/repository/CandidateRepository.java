package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.Candidate;
import com.klu.jobtracker.entity.CandidateStage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findAllByOrganizationIdOrderByUpdatedAtDesc(Long organizationId);
    List<Candidate> findTop6ByOrganizationIdOrderByUpdatedAtDesc(Long organizationId);
    Optional<Candidate> findByIdAndOrganizationId(Long id, Long organizationId);
    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStage(Long organizationId, CandidateStage stage);
    boolean existsByOrganizationIdAndJobOpeningId(Long organizationId, Long jobOpeningId);
}
