package com.cafeAurora.service;

import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Role;
import com.cafeAurora.model.User;
import com.cafeAurora.repository.IRoleRepository;
import com.cafeAurora.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
	private final IUserRepository userRepository;
	private final IRoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final RestTemplate restTemplate;

	@Value("${supabase.url}")
	private String supabaseUrl;
	
	@Value("${supabase.service.key}")
	private String serviceKey;
	
	public User getOne(UUID id) {
		return userRepository.findById(id).orElseThrow();
	}

	/* RECEPTIONIST */
	public List<User> getAllRecepctionist() {
		Role role = roleRepository.findByNameRole("R")
				.orElseThrow(() -> new RuntimeException("Rol Recepcionista no encontrado"));
		return userRepository.findByRole(role);
	}

	@Transactional
	public ResultResponse createReceptionist(User request) {
		if (userRepository.existsByEmail(request.getEmail()))
			throw new RuntimeException("El correo ya está registrado.");
		if (userRepository.existsByPhone(request.getPhone()))
			throw new RuntimeException("El teléfono ya está registrado.");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", serviceKey);
		headers.set("Authorization", "Bearer " + serviceKey);
		
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("name", request.getName());
		metadata.put("phone", request.getPhone());

		Map<String, Object> supabaseRequest = new HashMap<>();
		supabaseRequest.put("email", request.getEmail());
		supabaseRequest.put("password", request.getPassword());
		supabaseRequest.put("email_confirm", true);
		supabaseRequest.put("user_metadata", metadata);

		ResponseEntity<Map> response = restTemplate.exchange(supabaseUrl + "/auth/v1/admin/users", HttpMethod.POST,
				new HttpEntity<>(supabaseRequest, headers), Map.class);

		Map<String, Object> body = response.getBody();
		String userId = (String) body.get("id");
		
		Role role = roleRepository.findByNameRole("R")
				.orElseThrow(() -> new RuntimeException("Rol Recepcionista no encontrado"));

		request.setIdUser(UUID.fromString(userId));
		request.setRole(role);
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		request.setIsActive(true);
		String message = "Recepcionista creada correctamente";
		userRepository.save(request);
		return new ResultResponse(true, message);
	}

	public ResultResponse toggleStatusReceptionist(UUID id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
		String action = user.getIsActive() ? "Desactivado" : "Activado";
		user.setIsActive(!user.getIsActive());

		try {
			User deleted = userRepository.save(user);
			String message = String.format("%s ha sido %s correctamente", deleted.getName(), action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
}
