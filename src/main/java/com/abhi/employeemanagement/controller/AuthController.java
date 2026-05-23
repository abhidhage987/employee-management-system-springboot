package com.abhi.employeemanagement.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.abhi.employeemanagement.dto.AuthResponse;
import com.abhi.employeemanagement.dto.LoginRequest;
import com.abhi.employeemanagement.dto.RegisterRequest;
import com.abhi.employeemanagement.entity.Role;
import com.abhi.employeemanagement.entity.User;
import com.abhi.employeemanagement.repository.UserRepository;
import com.abhi.employeemanagement.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok(
                "User Registered Successfully"
        );
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        User user =
                userRepository.findByEmail(
                                request.getEmail())
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.badRequest()
                    .build();
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            return ResponseEntity.badRequest()
                    .build();
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }
}