package com.cafeAurora.controller;

import com.cafeAurora.dto.LoginRequest;
import com.cafeAurora.dto.RegisterRequest;
import com.cafeAurora.dto.AuthResponse;
import com.cafeAurora.dto.CompleteGoogleProfileRequest;
import com.cafeAurora.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
		return ResponseEntity.ok(authService.getCurrentUser(token));
	}
	
	@PostMapping("/google")
	public ResponseEntity<AuthResponse> loginWithGoogle(
	    @RequestHeader("Authorization") String authHeader
	) {
	    String token = authHeader.replace("Bearer ", "");
	    return ResponseEntity.ok(authService.loginWithGoogle(token));
	}
	
	@PostMapping("/google/complete")
	public ResponseEntity<AuthResponse> completeGoogleProfile(
	        @RequestHeader("Authorization") String authHeader,
	        @RequestBody CompleteGoogleProfileRequest request) {

	    String token = authHeader.replace("Bearer ", "");

	    AuthResponse response = authService.completeGoogleProfile(
	        token,
	        request.getUserId(),   
	        request.getPhone(),
	        request.getPassword()
	    );

	    return ResponseEntity.ok(response);
	}
}
