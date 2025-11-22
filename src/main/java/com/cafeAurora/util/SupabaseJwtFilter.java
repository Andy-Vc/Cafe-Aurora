package com.cafeAurora.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cafeAurora.model.User;
import com.cafeAurora.repository.IUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SupabaseJwtFilter extends OncePerRequestFilter {
	private final IUserRepository userRepository;
	private final String jwtSecret;
	private final String supabaseUrl;

	public SupabaseJwtFilter(IUserRepository userRepository, @Value("${supabase.jwt.secret}") String jwtSecret,
			@Value("${supabase.url}") String supabaseUrl) {
		this.userRepository = userRepository;
		this.jwtSecret = jwtSecret;
		this.supabaseUrl = supabaseUrl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String token = authHeader.substring(7);

			Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
			DecodedJWT jwt = JWT.require(algorithm).withIssuer(supabaseUrl + "/auth/v1").build().verify(token);

			String userId = jwt.getSubject();

			User user = userRepository.findById(UUID.fromString(userId))
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			String role = user.getRole().getNameRole();

			List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
					authorities);

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception e) {
			System.out.println("Error al validar token: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Token JWT inválido: " + e.getMessage());
			return;
		}

		filterChain.doFilter(request, response);
	}
}