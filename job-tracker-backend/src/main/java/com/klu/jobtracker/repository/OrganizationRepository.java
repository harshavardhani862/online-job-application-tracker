package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {}
