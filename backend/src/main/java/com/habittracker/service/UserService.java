package com.habittracker.service;

import com.habittracker.config.ApiException;
import com.habittracker.dto.AuthDtos.AuthResponse;
import com.habittracker.dto.UserDtos.ProfileResponse;
import com.habittracker.dto.UserDtos.UpdateProfileRequest;
import com.habittracker.entity.User;
import com.habittracker.repository.UserRepository;
import com.habittracker.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ProfileResponse getProfile(User user) {
        return new ProfileResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public AuthResponse updateProfile(User user, UpdateProfileRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Trenutna lozinka nije ispravna");
        }

        if (request.newUsername() != null && !request.newUsername().isBlank()
                && !request.newUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.newUsername())) {
                throw new ApiException(HttpStatus.CONFLICT, "Korisničko ime je već zauzeto");
            }
            user.setUsername(request.newUsername());
        }

        if (request.newEmail() != null && !request.newEmail().isBlank()
                && !request.newEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.newEmail())) {
                throw new ApiException(HttpStatus.CONFLICT, "Email je već registriran");
            }
            user.setEmail(request.newEmail());
        }

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }
}
