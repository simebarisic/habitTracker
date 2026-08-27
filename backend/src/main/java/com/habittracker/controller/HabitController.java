package com.habittracker.controller;

import com.habittracker.dto.HabitDtos.*;
import com.habittracker.entity.User;
import com.habittracker.security.CurrentUserProvider;
import com.habittracker.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;
    private final CurrentUserProvider currentUserProvider;

    public HabitController(HabitService habitService, CurrentUserProvider currentUserProvider) {
        this.habitService = habitService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> list(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(habitService.listHabits(user, includeInactive));
    }

    @PostMapping
    public ResponseEntity<HabitResponse> create(@Valid @RequestBody CreateHabitRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(habitService.createHabit(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateHabitRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(habitService.updateHabit(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        habitService.deleteHabit(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<LogsMatrixResponse> logs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(habitService.getLogsMatrix(user, from, to));
    }

    @PostMapping("/logs/toggle")
    public ResponseEntity<Void> toggleLog(@RequestBody ToggleLogRequest request) {
        User user = currentUserProvider.getCurrentUser();
        habitService.toggleLog(user, request);
        return ResponseEntity.noContent().build();
    }
}
