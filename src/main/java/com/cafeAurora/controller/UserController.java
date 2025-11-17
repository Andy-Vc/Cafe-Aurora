package com.cafeAurora.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
