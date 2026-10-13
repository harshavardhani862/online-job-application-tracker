package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.TeamInvitation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    Optional<TeamInvitation> findByToken(String token);
    List<TeamInvitation> findAllByOrganizationIdAndAcceptedAtIsNullOrderByCreatedAtDesc(Long organizationId);
    boolean existsByOrganizationIdAndEmailAndAcceptedAtIsNullAndExpiresAtAfter(
            Long organizationId, String email, LocalDateTime now);
}
