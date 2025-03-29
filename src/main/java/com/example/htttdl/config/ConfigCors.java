package com.example.htttdl.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigCors {

	// @Bean
	// public CorsFilter corsFilter() {
	// UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// CorsConfiguration config = new CorsConfiguration();

	// config.setAllowCredentials(true);
	// config.setAllowedOrigins(List.of("http://localhost:3000",
	// "https://your-frontend.com")); // Chỉ định origin cụ
	// // thể
	// config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	// config.setAllowedHeaders(List.of("*"));

	// source.registerCorsConfiguration("/**", config);
	// return new CorsFilter(source);
	// }

	// @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
	// CorsConfiguration configuration = new CorsConfiguration();
	// configuration.setAllowCredentials(true);
	// configuration.setAllowedOrigins(List.of("http://localhost:3000",
	// "https://your-frontend.com")); // 👈 Thay
	// // domain tương
	// // ứng
	// configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE",
	// "OPTIONS"));
	// configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

	// UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// source.registerCorsConfiguration("/**", configuration);
	// return source;
	// }
}
