package com.habittracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HabitDtos {

    public record CreateHabitRequest(
            @NotBlank @Size(max = 100) String name,
            String icon
    ) {}

    public record UpdateHabitRequest(
            @NotBlank @Size(max = 100) String name,
            String icon,
            Integer sortOrder,
            Boolean active
    ) {}

    public record HabitResponse(
            Long id,
            String name,
            String icon,
            Integer sortOrder,
            Boolean active,
            LocalDateTime createdAt
    ) {}

    public record ToggleLogRequest(
            Long habitId,
            LocalDate date,
            Boolean completed
    ) {}

    // date -> habitId -> completed
    public record LogsMatrixResponse(
            List<HabitResponse> habits,
            Map<String, Map<Long, Boolean>> entries
    ) {}

    public record HabitStat(
            Long habitId,
            String name,
            long totalCompletions,
            int currentStreak,
            int longestStreak,
            double completionRatePercent
    ) {}

    public record DailyProgress(
            LocalDate date,
            int completedCount,
            int totalHabits,
            double percent
    ) {}

    public record StatsResponse(
            List<HabitStat> habitStats,
            List<DailyProgress> dailyProgress
    ) {}
}
