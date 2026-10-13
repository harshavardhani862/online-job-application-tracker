package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.TeamMembership;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    Optional<TeamMembership> findByUserId(Long userId);
    List<TeamMembership> findAllByOrganizationIdOrderByJoinedAtAsc(Long organizationId);
    boolean existsByOrganizationIdAndUserEmail(Long organizationId, String email);
}
