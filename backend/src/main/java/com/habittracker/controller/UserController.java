package com.habittracker.controller;

import com.habittracker.dto.AuthDtos.AuthResponse;
import com.habittracker.dto.UserDtos.ProfileResponse;
import com.habittracker.dto.UserDtos.UpdateProfileRequest;
import com.habittracker.entity.User;
import com.habittracker.security.CurrentUserProvider;
import com.habittracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UserController(UserService userService, CurrentUserProvider currentUserProvider) {
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me() {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }
}
