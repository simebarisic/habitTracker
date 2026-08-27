package com.habittracker.service;

import com.habittracker.config.ApiException;
import com.habittracker.dto.HabitDtos.*;
import com.habittracker.entity.Habit;
import com.habittracker.entity.HabitLog;
import com.habittracker.entity.User;
import com.habittracker.repository.HabitLogRepository;
import com.habittracker.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    public HabitService(HabitRepository habitRepository, HabitLogRepository habitLogRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
    }

    public List<HabitResponse> listHabits(User user, boolean includeInactive) {
        List<Habit> habits = includeInactive
                ? habitRepository.findByUserOrderBySortOrderAsc(user)
                : habitRepository.findByUserAndActiveTrueOrderBySortOrderAsc(user);
        return habits.stream().map(this::toResponse).toList();
    }

    @Transactional
    public HabitResponse createHabit(User user, CreateHabitRequest request) {
        String name = request.name().trim();
        if (name.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Habit name cannot be blank");
        }
        if (habitRepository.existsByUserAndNameIgnoreCase(user, name)) {
            throw new ApiException(HttpStatus.CONFLICT, "You already have a habit with this name");
        }

        int nextSortOrder = habitRepository.findByUserOrderBySortOrderAsc(user).size() + 1;

        Habit habit = new Habit();
        habit.setUser(user);
        habit.setName(name);
        habit.setIcon(request.icon());
        habit.setSortOrder(nextSortOrder);
        habit.setActive(true);
        habitRepository.save(habit);
        return toResponse(habit);
    }

    @Transactional
    public HabitResponse updateHabit(User user, Long id, UpdateHabitRequest request) {
        Habit habit = habitRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        habit.setName(request.name().trim());
        if (request.icon() != null) habit.setIcon(request.icon());
        if (request.sortOrder() != null) habit.setSortOrder(request.sortOrder());
        if (request.active() != null) habit.setActive(request.active());

        habitRepository.save(habit);
        return toResponse(habit);
    }

    @Transactional
    public void deleteHabit(User user, Long id) {
        Habit habit = habitRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));
        habitRepository.delete(habit);
    }

    @Transactional
    public void toggleLog(User user, ToggleLogRequest request) {
        Habit habit = habitRepository.findByIdAndUser(request.habitId(), user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        LocalDate date = request.date() != null ? request.date() : LocalDate.now();
        boolean completed = request.completed() == null || request.completed();

        if (completed) {
            HabitLog log = habitLogRepository.findByHabitAndLogDate(habit, date)
                    .orElseGet(() -> {
                        HabitLog l = new HabitLog();
                        l.setHabit(habit);
                        l.setLogDate(date);
                        return l;
                    });
            log.setCompleted(true);
            habitLogRepository.save(log);
        } else {
            habitLogRepository.deleteByHabitAndLogDate(habit, date);
        }
    }

    public LogsMatrixResponse getLogsMatrix(User user, LocalDate from, LocalDate to) {
        List<Habit> habits = habitRepository.findByUserOrderBySortOrderAsc(user);
        List<HabitLog> logs = habitLogRepository.findAllForUserBetween(user.getId(), from, to);

        Map<String, Map<Long, Boolean>> entries = new LinkedHashMap<>();
        for (HabitLog log : logs) {
            entries
                    .computeIfAbsent(log.getLogDate().toString(), d -> new LinkedHashMap<>())
                    .put(log.getHabit().getId(), log.getCompleted());
        }

        return new LogsMatrixResponse(habits.stream().map(this::toResponse).toList(), entries);
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getIcon(),
                habit.getSortOrder(),
                habit.getActive(),
                habit.getCreatedAt()
        );
    }
}
