package com.cafeAurora.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.User;
import com.cafeAurora.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getItem(@PathVariable("id") UUID id) {
		try {
			User getUser = userService.getOne(id);
			if (getUser == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ninguna usuario con ID: " + id);
			}
			return ResponseEntity.ok(getUser);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener el usuario con ID: " + id);
		}
	}

	/* RECEPTIONIST */
	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllReceptionist() {
		try {
			return ResponseEntity.ok(userService.getAllRecepctionist());
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<ResultResponse> createReceptionist(@RequestBody User user) {
		try {
			ResultResponse result = userService.createReceptionist(user);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResultResponse(false, e.getMessage()));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResultResponse> deleteReceptionist(@PathVariable("id") UUID id) {
		try {
			ResultResponse result = userService.toggleStatusReceptionist(id);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
