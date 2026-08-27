package com.habittracker.service;

import com.habittracker.dto.HabitDtos.DailyProgress;
import com.habittracker.dto.HabitDtos.HabitStat;
import com.habittracker.dto.HabitDtos.StatsResponse;
import com.habittracker.entity.Habit;
import com.habittracker.entity.HabitLog;
import com.habittracker.entity.User;
import com.habittracker.repository.HabitLogRepository;
import com.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class StatsService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    public StatsService(HabitRepository habitRepository, HabitLogRepository habitLogRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
    }

    public StatsResponse computeStats(User user, LocalDate from, LocalDate to) {
        List<Habit> habits = habitRepository.findByUserAndActiveTrueOrderBySortOrderAsc(user);

        List<HabitStat> habitStats = new ArrayList<>();
        // date -> count of completed habits that day (within the active habit set)
        Map<LocalDate, Integer> completedPerDay = new TreeMap<>();

        long periodDays = ChronoUnit.DAYS.between(from, to) + 1;

        for (Habit habit : habits) {
            List<HabitLog> allLogs = habitLogRepository.findByHabitOrderByLogDateAsc(habit);
            Set<LocalDate> completedDates = new HashSet<>();
            for (HabitLog log : allLogs) {
                if (Boolean.TRUE.equals(log.getCompleted())) {
                    completedDates.add(log.getLogDate());
                    if (!log.getLogDate().isBefore(from) && !log.getLogDate().isAfter(to)) {
                        completedPerDay.merge(log.getLogDate(), 1, Integer::sum);
                    }
                }
            }

            long totalCompletionsInPeriod = completedDates.stream()
                    .filter(d -> !d.isBefore(from) && !d.isAfter(to))
                    .count();

            int currentStreak = computeCurrentStreak(completedDates);
            int longestStreak = computeLongestStreak(completedDates);
            double rate = periodDays > 0 ? (totalCompletionsInPeriod * 100.0 / periodDays) : 0.0;

            habitStats.add(new HabitStat(
                    habit.getId(),
                    habit.getName(),
                    totalCompletionsInPeriod,
                    currentStreak,
                    longestStreak,
                    Math.round(rate * 10.0) / 10.0
            ));
        }

        List<DailyProgress> dailyProgress = new ArrayList<>();
        int totalHabits = habits.size();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            int completed = completedPerDay.getOrDefault(d, 0);
            double percent = totalHabits > 0 ? Math.round((completed * 1000.0 / totalHabits)) / 10.0 : 0.0;
            dailyProgress.add(new DailyProgress(d, completed, totalHabits, percent));
        }

        return new StatsResponse(habitStats, dailyProgress);
    }

    private int computeCurrentStreak(Set<LocalDate> completedDates) {
        if (completedDates.isEmpty()) return 0;
        LocalDate today = LocalDate.now();
        LocalDate cursor = completedDates.contains(today) ? today : today.minusDays(1);
        if (!completedDates.contains(cursor)) return 0;

        int streak = 0;
        while (completedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private int computeLongestStreak(Set<LocalDate> completedDates) {
        if (completedDates.isEmpty()) return 0;
        List<LocalDate> sorted = new ArrayList<>(completedDates);
        Collections.sort(sorted);

        int longest = 1;
        int current = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i - 1).plusDays(1).equals(sorted.get(i))) {
                current++;
            } else {
                current = 1;
            }
            longest = Math.max(longest, current);
        }
        return longest;
    }
}
