package com.abhi.employeemanagement.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.abhi.employeemanagement.dto.AuthResponse;
import com.abhi.employeemanagement.dto.LoginRequest;
import com.abhi.employeemanagement.dto.LoginResponse;
import com.abhi.employeemanagement.dto.RefreshTokenRequest;
import com.abhi.employeemanagement.dto.RegisterRequest;
import com.abhi.employeemanagement.entity.RefreshToken;
import com.abhi.employeemanagement.entity.Role;
import com.abhi.employeemanagement.entity.User;
import com.abhi.employeemanagement.repository.RefreshTokenRepository;
import com.abhi.employeemanagement.repository.UserRepository;
import com.abhi.employeemanagement.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private RefreshTokenRepository refreshTokenRepository;

	private JwtService jwtService;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

		User user = new User();

		user.setName(request.getName());

		user.setEmail(request.getEmail());

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setRole(Role.USER);

		userRepository.save(user);

		return ResponseEntity.ok("User Registered Successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user == null) {

			return ResponseEntity.badRequest().build();
		}

		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!passwordMatches) {

			return ResponseEntity.badRequest().build();
		}

		String accessToken = jwtService.generateToken(user.getEmail());

		String refreshTokenValue = jwtService.generateRefreshToken(user.getEmail());

		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setToken(refreshTokenValue);

		refreshToken.setUser(user);

		refreshToken.setExpiryDate(

				LocalDateTime.now().plusDays(7));

		refreshTokenRepository.save(refreshToken);

		return ResponseEntity.ok(

				new LoginResponse(accessToken, refreshTokenValue));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(

			@RequestBody RefreshTokenRequest request) {

		RefreshToken refreshToken =

				refreshTokenRepository.findByToken(request.getRefreshToken()).orElse(null);

		if (refreshToken == null) {

			return ResponseEntity.badRequest().body("Invalid Refresh Token");
		}

		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {

			return ResponseEntity.badRequest().body("Refresh Token Expired");
		}

		String accessToken = jwtService.generateToken(

				refreshToken.getUser().getEmail());

		return ResponseEntity.ok(new AuthResponse(accessToken));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(

	        @RequestBody
	        RefreshTokenRequest request) {

	    refreshTokenRepository
	            .deleteByToken(
	                    request.getRefreshToken()
	            );

	    return ResponseEntity.ok(
	            "Logged out successfully"
	    );
	}
}