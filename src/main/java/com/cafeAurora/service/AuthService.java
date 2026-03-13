package com.cafeAurora.service;

import com.cafeAurora.dto.LoginRequest;
import com.cafeAurora.dto.RegisterRequest;
import com.cafeAurora.dto.AuthResponse;
import com.cafeAurora.dto.UserResponse;
import com.cafeAurora.exception.IncompleteProfileException;
import com.cafeAurora.model.User;
import com.cafeAurora.model.Role;
import com.cafeAurora.repository.IUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.cafeAurora.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final IUserRepository userRepository;
	private final IRoleRepository roleRepository;
	private final RestTemplate restTemplate;
	private final BCryptPasswordEncoder passwordEncoder;

	@Value("${supabase.jwt.secret}")
	private String supabaseJwtSecret;

	@Value("${supabase.url}")
	private String supabaseUrl;

	@Value("${supabase.key}")
	private String supabaseKey;

	@Value("${supabase.anon.key}")
	private String anonKey;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("El correo ingresado ya se encuentra registrado.");
		}

		if (userRepository.existsByPhone(request.getPhone())) {
			throw new RuntimeException("El número telefónico ya está registrado.");
		}
		// 1. Registrar en Supabase Auth (auth.users)
		Map<String, Object> supabaseAuthRequest = new HashMap<>();
		supabaseAuthRequest.put("email", request.getEmail());
		supabaseAuthRequest.put("password", request.getPassword());
		supabaseAuthRequest.put("email_confirm", true);
		// Metadata adicional para Supabase
		Map<String, String> userMetadata = new HashMap<>();
		userMetadata.put("name", request.getName());
		userMetadata.put("phone", request.getPhone());
		supabaseAuthRequest.put("user_metadata", userMetadata);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", anonKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(supabaseAuthRequest, headers);

		try {
			// Llamar a Supabase Auth API
			ResponseEntity<Map> supabaseResponse = restTemplate.exchange(supabaseUrl + "/auth/v1/signup",
					HttpMethod.POST, entity, Map.class);

			Map<String, Object> responseBody = supabaseResponse.getBody();
			Map<String, Object> userData = (Map<String, Object>) responseBody.get("user");
			String userId = (String) userData.get("id");

			// 2. Insertar en TB_USERS con el mismo UUID
			Role clientRole = roleRepository.findByNameRole("C")
					.orElseThrow(() -> new RuntimeException("Rol Cliente no encontrado"));

			User user = new User();
			user.setIdUser(UUID.fromString(userId));
			user.setRole(clientRole);
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setName(request.getName());
			user.setPhone(request.getPhone());
			user.setIsActive(true);

			userRepository.save(user);

			// 3. Retornar respuesta con token de Supabase
			Map<String, Object> session = (Map<String, Object>) responseBody.get("session");
			if (session == null) {
				// Obtener token iniciando sesión
				Map<String, Object> loginRequest = Map.of("email", request.getEmail(), "password",
						request.getPassword());
				ResponseEntity<Map> loginResponse = restTemplate.exchange(
						supabaseUrl + "/auth/v1/token?grant_type=password", HttpMethod.POST,
						new HttpEntity<>(loginRequest, headers), Map.class);
				session = loginResponse.getBody();
			}
			String accessToken = (String) session.get("access_token");

			return AuthResponse.builder().token(accessToken).userId(userId).email(request.getEmail())
					.name(request.getName()).role(clientRole.getNameRole())
					.message("Tu cuenta ha sido creada correctamente. ¡Bienvenido a Café Aurora!").build();

		} catch (Exception e) {
			throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
		}
	}

	public AuthResponse login(LoginRequest request) {
		Map<String, String> supabaseAuthRequest = new HashMap<>();
		supabaseAuthRequest.put("email", request.getEmail());
		supabaseAuthRequest.put("password", request.getPassword());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", supabaseKey);

		HttpEntity<Map<String, String>> entity = new HttpEntity<>(supabaseAuthRequest, headers);

		try {
			ResponseEntity<Map> supabaseResponse = restTemplate
					.exchange(supabaseUrl + "/auth/v1/token?grant_type=password", HttpMethod.POST, entity, Map.class);

			Map<String, Object> responseBody = supabaseResponse.getBody();
			Map<String, Object> userData = (Map<String, Object>) responseBody.get("user");
			String userId = (String) userData.get("id");
			String accessToken = (String) responseBody.get("access_token");

			User user = userRepository.findById(UUID.fromString(userId))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado en TB_USERS"));

			if (user.getPassword() == null) {
				throw new RuntimeException(
						"Esta cuenta fue creada con Google. Usa 'Continuar con Google' para iniciar sesión.");
			}
			
			if (!user.getIsActive()) {
				throw new RuntimeException("Usuario inactivo");
			}

			return AuthResponse.builder().token(accessToken).userId(userId).email(user.getEmail()).name(user.getName())
					.phone(user.getPhone()).role(user.getRole().getNameRole())
					.message("Inicio de sesión exitoso. ¡Bienvenido de nuevo, " + user.getName() + "!").build();

		} catch (HttpClientErrorException e) {
			String body = e.getResponseBodyAsString();
			if (body.contains("invalid_credentials")) {
				throw new RuntimeException("Correo o contraseña incorrectos");
			}
			throw new RuntimeException("Error en autenticación Supabase: " + body);
		} catch (Exception e) {
			throw new RuntimeException("Error interno al iniciar sesión");
		}
	}

	@Transactional(noRollbackFor = IncompleteProfileException.class)
	public AuthResponse loginWithGoogle(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("apikey", supabaseKey);
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(supabaseUrl + "/auth/v1/user", HttpMethod.GET, entity,
				Map.class);

		Map<String, Object> userData = response.getBody();
		String userId = (String) userData.get("id");
		String email = (String) userData.get("email");

		Map<String, Object> userMetadata = (Map<String, Object>) userData.get("user_metadata");
		String name = userMetadata != null ? (String) userMetadata.getOrDefault("full_name", email) : email;

		Optional<User> existingUser = userRepository.findById(UUID.fromString(userId));

		User user;
		if (existingUser.isPresent()) {
	        user = existingUser.get();
	    } else {
			Role clientRole = roleRepository.findByNameRole("C")
					.orElseThrow(() -> new RuntimeException("Rol no encontrado"));

			user = new User();
			user.setIdUser(UUID.fromString(userId));
			user.setRole(clientRole);
			user.setEmail(email);
			user.setName(name);
			user.setPassword(null);
			user.setPhone(null);
			user.setIsActive(true);
			userRepository.saveAndFlush(user);
		}

		if (!user.getIsActive()) {
			throw new RuntimeException("Usuario inactivo");
		}

		if (user.getPhone() == null || user.getPassword() == null) {
			throw new IncompleteProfileException(accessToken, userId, email, user.getName());
		}

		return AuthResponse.builder().token(accessToken).userId(userId).email(email).name(user.getName())
				.role(user.getRole().getNameRole()).message("¡Bienvenido de nuevo, " + user.getName() + "!").build();
	}

	@Transactional
	public AuthResponse completeGoogleProfile(String accessToken, String userId, 
	                                           String phone, String password) {
	    User user = userRepository.findById(UUID.fromString(userId))
	        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    if (userRepository.existsByPhone(phone)) {
	        throw new RuntimeException("El número telefónico ya está registrado.");
	    }

	    user.setPhone(phone);
	    user.setPassword(passwordEncoder.encode(password));
	    userRepository.save(user);

	    return AuthResponse.builder()
	        .token(accessToken)
	        .userId(userId)
	        .email(user.getEmail())
	        .name(user.getName())
	        .phone(phone)
	        .role(user.getRole().getNameRole())
	        .message("¡Perfil completado! Bienvenido a Café Aurora, " + user.getName() + "!")
	        .build();
	}

	public UserResponse getCurrentUser(String authHeader) {
		String token = authHeader.replace("Bearer ", "");

		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(supabaseJwtSecret.getBytes())).build()
					.parseClaimsJws(token).getBody();

			String userId = claims.getSubject(); 

			User user = userRepository.findById(UUID.fromString(userId))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			return UserResponse.builder().idUser(user.getIdUser()).email(user.getEmail()).name(user.getName())
					.phone(user.getPhone()).role(user.getRole().getNameRole()).isActive(user.getIsActive()).build();

		} catch (Exception e) {
			throw new RuntimeException("Token JWT inválido: " + e.getMessage());
		}
	}
}
