package com.rahman.arctic.orca;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rahman.arctic.orca.filters.JwtRequestFilter;
import com.rahman.arctic.orca.utils.ArcticUserService;
import com.rahman.arctic.orca.utils.JwtAuthenticationEntryPoint;

@Service
@EnableWebSecurity
public class Orca {

	private final JwtRequestFilter jwtFilter;
	private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;

	public Orca(JwtRequestFilter jwt, JwtAuthenticationEntryPoint entryPoint) {
		System.out.println("Enabling Service: Orca");
		jwtFilter = jwt;
		jwtAuthEntryPoint = entryPoint;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(15, new SecureRandom());
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(ArcticUserService aus) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(aus);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:5173", "http://localhost")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> {})
			.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/range-api/v1/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/range-api/v1/csrf-token").permitAll()
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}