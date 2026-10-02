package com.spring_microservice.auth_service.service;



import com.spring_microservice.auth_service.dto.LoginRequest;
import com.spring_microservice.auth_service.dto.LoginResponse;
import com.spring_microservice.auth_service.dto.RegisterRequest;
import com.spring_microservice.auth_service.dto.RegisterResponse;
import com.spring_microservice.auth_service.entity.User;
import com.spring_microservice.auth_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ==========================================
    // REGISTER
    // ==========================================

    public RegisterResponse register(
            RegisterRequest request
    ) {

        if (userRepository
                .findByEmail(request.getEmail())
                .isPresent()) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user = new User(
                request.getEmail(),
                encodedPassword,
                "USER"
        );

        User savedUser =
                userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // ==========================================
    // LOGIN
    // ==========================================

    public LoginResponse login(
            LoginRequest request
    ) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid credentials"
                        )
                );

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        return new LoginResponse(
                token,
                "Bearer",
                user.getEmail(),
                user.getRole()
        );
    }
} 
