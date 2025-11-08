package com.cafeAurora.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cafeAurora.util.SupabaseJwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SupabaseJwtFilter supabaseJwtFilter;

    public SecurityConfig(SupabaseJwtFilter supabaseJwtFilter) {
        this.supabaseJwtFilter = supabaseJwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/menu/**").permitAll()
                // Rutas que requieren autenticación
                .requestMatchers("/reservations/**").authenticated()
                .requestMatchers("/profile/**").authenticated()
                
                // Rutas solo para Recepcionista (R)
                .requestMatchers("/admin/reservations/**").hasAuthority("R")
                
                // Rutas solo para Administrador (A)
                .requestMatchers("/admin/users/**").hasAuthority("A")
                .requestMatchers("/admin/menu/**").hasAuthority("A")
                
                .anyRequest().authenticated()
            )
            
            .sessionManagement(sess -> 
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http.addFilterBefore(supabaseJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
