package com.habittracker.repository;

import com.habittracker.entity.Habit;
import com.habittracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserAndActiveTrueOrderBySortOrderAsc(User user);
    List<Habit> findByUserOrderBySortOrderAsc(User user);
    Optional<Habit> findByIdAndUser(Long id, User user);
    boolean existsByUserAndNameIgnoreCase(User user, String name);
}
