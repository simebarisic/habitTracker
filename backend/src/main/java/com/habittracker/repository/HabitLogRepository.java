package com.habittracker.repository;

import com.habittracker.entity.Habit;
import com.habittracker.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitAndLogDate(Habit habit, LocalDate logDate);

    @Query("""
            SELECT hl FROM HabitLog hl
            WHERE hl.habit.user.id = :userId
              AND hl.logDate BETWEEN :from AND :to
            ORDER BY hl.logDate ASC
            """)
    List<HabitLog> findAllForUserBetween(@Param("userId") Long userId,
                                          @Param("from") LocalDate from,
                                          @Param("to") LocalDate to);

    List<HabitLog> findByHabitOrderByLogDateAsc(Habit habit);

    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit = :habit AND hl.logDate <= :date ORDER BY hl.logDate DESC")
    List<HabitLog> findByHabitUpToDateDesc(@Param("habit") Habit habit, @Param("date") LocalDate date);

    long countByHabitAndCompletedTrue(Habit habit);

    void deleteByHabitAndLogDate(Habit habit, LocalDate logDate);
}
