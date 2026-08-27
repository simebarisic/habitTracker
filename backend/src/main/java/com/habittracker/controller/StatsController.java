package com.habittracker.controller;

import com.habittracker.dto.HabitDtos.StatsResponse;
import com.habittracker.entity.User;
import com.habittracker.security.CurrentUserProvider;
import com.habittracker.service.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final CurrentUserProvider currentUserProvider;

    public StatsController(StatsService statsService, CurrentUserProvider currentUserProvider) {
        this.statsService = statsService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ResponseEntity<StatsResponse> stats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(statsService.computeStats(user, from, to));
    }
}
