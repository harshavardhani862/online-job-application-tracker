package com.klu.jobtracker.repository;

import com.klu.jobtracker.entity.Reminder;
import com.klu.jobtracker.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    @EntityGraph(attributePaths = {"application", "application.user"})
    List<Reminder> findByApplication_UserOrderByReminderDateTimeAsc(User user);

    @EntityGraph(attributePaths = {"application", "application.user"})
    Optional<Reminder> findByIdAndApplication_User(Long id, User user);

    long countByApplication_UserAndCompletedFalse(User user);

    @EntityGraph(attributePaths = {"application", "application.user"})
    List<Reminder> findTop100ByReminderDateTimeLessThanEqualAndCompletedFalseAndNotificationSentFalseOrderByReminderDateTimeAsc(
            LocalDateTime dateTime);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM Reminder r WHERE r.application.id = :applicationId")
    int deleteAllForApplication(@Param("applicationId") Long applicationId);
}
