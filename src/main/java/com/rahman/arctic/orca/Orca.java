package com.rahman.arctic.orca;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rahman.arctic.orca.filters.CookieFilter;
import com.rahman.arctic.orca.filters.JwtRequestFilter;
import com.rahman.arctic.orca.utils.IUserService;

@Configuration
@EnableJpaRepositories(basePackages = {"com.rahman.arctic.orca.repos"})
@EntityScan("com.rahman.arctic.orca.objects")
@EnableWebSecurity
public class Orca {

	public Orca() {
		System.out.println("Enabling Service: Orca");
	}
	
	@Autowired
	private JwtRequestFilter jwtFilter;

	@Autowired
	private CookieFilter cookieFilter;

	@Bean
	UserDetailsService userDetailsService() {
		return new IUserService();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(15, new SecureRandom());
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:5173").allowedHeaders("*").allowCredentials(true);;
            }
        };
    }
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> 
				csrf.disable()
				//csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			)
			.authorizeHttpRequests(authorizeRequests -> 
				authorizeRequests
					.requestMatchers(HttpMethod.POST, "/range-api/v1/authenticate").permitAll()
					.requestMatchers(HttpMethod.GET, "/range-api/v1/csrf-token").permitAll()
					.requestMatchers(HttpMethod.POST, "/range-api/v1/regularUser").hasAnyAuthority("ADMIN", "USER")
					.requestMatchers(HttpMethod.GET, "/range-api/v1/exercise").hasAnyAuthority("ADMIN", "USER")
					.requestMatchers("/iceberg-api/v1/**", "/range-api/v1/provider/*").hasAnyAuthority("ADMIN", "USER")
					.anyRequest().authenticated()
			).sessionManagement(session -> 
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		
		http.addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
}