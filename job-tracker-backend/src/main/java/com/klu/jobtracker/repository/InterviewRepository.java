
package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.Interview;
import com.klu.jobtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplication_UserOrderByInterviewDateTimeAsc(
            User user);

    Optional<Interview> findByIdAndApplication_User(
            Long id, User user);

    long countByApplication_User(User user);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM Interview i WHERE i.application.id = :applicationId")
    int deleteAllForApplication(
            @Param("applicationId") Long applicationId);
}
