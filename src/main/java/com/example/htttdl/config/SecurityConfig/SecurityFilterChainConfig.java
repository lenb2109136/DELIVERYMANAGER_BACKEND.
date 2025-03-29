package com.example.htttdl.config.SecurityConfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityFilterChainConfig {
        // @Autowired
        // CorsConfigurationSource c;

        @Autowired
        private AuthenticationTokenProvider authenticationTokenProvider;

        @Autowired
        private AuthenticationTokenProviderCustomer authenticationTokenProviderCustomer;

        @Bean
        public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/login/nhanvien")
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        public SecurityFilterChain loginCustomerSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/login/customer")
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                        AuthenticationManager authenticationManager) throws Exception {
                http.securityMatcher("/admin/**")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 👈 Thêm CORS
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/login").permitAll()
                                                .anyRequest().hasRole("ADMIN"))
                                .headers(headers -> headers
                                                .frameOptions().disable())
                                .formLogin(form -> form.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .sessionManagement(session -> session.disable())
                                .addFilterBefore(new FilterOnePer(authenticationManager),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public SecurityFilterChain shipperSecurityFilterChain(HttpSecurity http,
                        AuthenticationManager authenticationManager)
                        throws Exception {
                http.securityMatcher("/shipper/**")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())
                                .formLogin(form -> form.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .sessionManagement(session -> session.disable())
                                .addFilterBefore(new FilterOnePer(authenticationManager),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public SecurityFilterChain CustomerSecurityFilterChain(HttpSecurity http,
                        AuthenticationManager authenticationManager)
                        throws Exception {
                http.securityMatcher("/customer/**")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/login").permitAll()
                                                .anyRequest().hasRole("CUSTOMER"))
                                .formLogin(form -> form.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .sessionManagement(session -> session.disable())
                                .addFilterBefore(new FilterCustomerOnper(authenticationManager),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager() {
                return new ProviderManager(List.of(authenticationTokenProvider, authenticationTokenProviderCustomer));
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://your-frontend.com")); // 👈
                                                                                                                // Thay
                                                                                                                // domain
                                                                                                                // tương
                                                                                                                // ứng
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}
// package com.example.htttdl.config;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.ProviderManager;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;
// import
// org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// public class SecurityFilterChainConfig {

// @Autowired
// private AuthenticationTokenProvider authenticationTokenProvider;

// @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http,
// AuthenticationManager authenticationManager) throws Exception {
// http
// .csrf(csrf -> csrf.disable()) // Tắt CSRF
// .cors(cors -> cors.disable()) // Tắt CORS
// .authorizeHttpRequests(auth -> auth
// .requestMatchers("/admin/login", "/customer/login",
// "/shipper/login").permitAll()
// .requestMatchers("/admin/**").hasRole("ADMIN")
// .requestMatchers("/shipper/**").hasRole("SHIPPER")
// .requestMatchers("/customer/**").hasRole("CUSTOMER")
// .anyRequest().authenticated()
// )
// .formLogin(form -> form.disable()) // Tắt form login
// .httpBasic(httpBasic -> httpBasic.disable()) // Tắt HTTP Basic Auth
// .sessionManagement(session -> session.disable()) // Tắt session
// .addFilterBefore(new FilterOnePer(authenticationManager),
// UsernamePasswordAuthenticationFilter.class)
// .addFilterBefore(new FilterCustomerOnper(authenticationManager),
// UsernamePasswordAuthenticationFilter.class);

// return http.build();
// }

// @Bean
// public AuthenticationManager authenticationManager() {
// return new ProviderManager(List.of(authenticationTokenProvider));
// }
// }
