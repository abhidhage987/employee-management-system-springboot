package com.abhi.employeemanagement.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.abhi.employeemanagement.dto.AuthResponse;
import com.abhi.employeemanagement.dto.ForgotPasswordRequest;
import com.abhi.employeemanagement.dto.LoginRequest;
import com.abhi.employeemanagement.dto.LoginResponse;
import com.abhi.employeemanagement.dto.OtpVerificationRequest;
import com.abhi.employeemanagement.dto.RefreshTokenRequest;
import com.abhi.employeemanagement.dto.RegisterRequest;
import com.abhi.employeemanagement.dto.ResetPasswordRequest;
import com.abhi.employeemanagement.entity.OtpVerification;
import com.abhi.employeemanagement.entity.RefreshToken;
import com.abhi.employeemanagement.entity.Role;
import com.abhi.employeemanagement.entity.User;
import com.abhi.employeemanagement.repository.OtpVerificationRepository;
import com.abhi.employeemanagement.repository.RefreshTokenRepository;
import com.abhi.employeemanagement.repository.UserRepository;
import com.abhi.employeemanagement.security.JwtService;
import com.abhi.employeemanagement.service.EmailService;
import com.abhi.employeemanagement.util.OtpUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private JwtService jwtService;
	private RefreshTokenRepository refreshTokenRepository;
	private EmailService emailService;
	private OtpVerificationRepository otpVerificationRepository;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			RefreshTokenRepository refreshTokenRepository, EmailService emailService,
			OtpVerificationRepository otpVerificationRepository) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.emailService = emailService;
		this.otpVerificationRepository = otpVerificationRepository;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(

			@RequestBody RegisterRequest request) {

		User user = new User();

		user.setName(request.getName());

		user.setEmail(request.getEmail());

		user.setPassword(

				passwordEncoder.encode(request.getPassword()));

		user.setRole(Role.USER);

		user.setEnabled(false);

		userRepository.save(user);

		String otp = OtpUtil.generateOtp();

		OtpVerification otpVerification = new OtpVerification();

		otpVerification.setEmail(request.getEmail());

		otpVerification.setOtp(otp);

		otpVerification.setVerified(false);

		otpVerification.setExpiryTime(

				LocalDateTime.now().plusMinutes(5));

		otpVerificationRepository.save(otpVerification);

		emailService.sendEmail(

				request.getEmail(),

				"OTP Verification",

				"Your OTP is : " + otp);

		return ResponseEntity.ok("OTP sent to email");
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user == null) {

			return ResponseEntity.badRequest().body(null);
		}

		
		if (!user.isEnabled()) {

			throw new RuntimeException("Please verify OTP first");
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

			@RequestBody RefreshTokenRequest request) {

		refreshTokenRepository.deleteByToken(request.getRefreshToken());

		return ResponseEntity.ok("Logged out successfully");
	}

	@GetMapping("/send-mail")
	public ResponseEntity<String> sendMail() {

		emailService.sendEmail(

				"abhidhage987@gmail.com",

				"Spring Boot Email Test",

				"Email sent successfully from Spring Boot");

		return ResponseEntity.ok("Email Sent Successfully");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(

			@RequestBody OtpVerificationRequest request) {

		OtpVerification otpVerification =

				otpVerificationRepository.findByEmail(request.getEmail()).orElse(null);

		if (otpVerification == null) {

			return ResponseEntity.badRequest().body("OTP not found");
		}

		if (!otpVerification.getOtp().equals(request.getOtp())) {

			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {

			return ResponseEntity.badRequest().body("OTP Expired");
		}

		otpVerification.setVerified(true);
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user != null) {

			user.setEnabled(true);

			userRepository.save(user);
		}

		otpVerificationRepository.save(otpVerification);

		return ResponseEntity.ok("OTP Verified Successfully");
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(

			@RequestBody ForgotPasswordRequest request) {

		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user == null) {

			return ResponseEntity.badRequest().body("User not found");
		}

		String otp = OtpUtil.generateOtp();

		OtpVerification otpVerification =

				otpVerificationRepository.findByEmail(request.getEmail()).orElse(new OtpVerification());

		otpVerification.setEmail(request.getEmail());

		otpVerification.setOtp(otp);

		otpVerification.setVerified(false);

		otpVerification.setExpiryTime(

				LocalDateTime.now().plusMinutes(5));

		otpVerificationRepository.save(otpVerification);

		emailService.sendEmail(

				request.getEmail(),

				"Reset Password OTP",

				"Your OTP is : " + otp);

		return ResponseEntity.ok("OTP sent to email");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(

			@RequestBody ResetPasswordRequest request) {

		OtpVerification otpVerification =

				otpVerificationRepository.findByEmail(request.getEmail()).orElse(null);

		if (otpVerification == null) {

			return ResponseEntity.badRequest().body("OTP not found");
		}

		if (!otpVerification.getOtp().equals(request.getOtp())) {

			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {

			return ResponseEntity.badRequest().body("OTP Expired");
		}

		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user == null) {

			return ResponseEntity.badRequest().body("User not found");
		}

		user.setPassword(

				passwordEncoder.encode(request.getNewPassword()));

		userRepository.save(user);

		return ResponseEntity.ok("Password Reset Successfully");
	}
}