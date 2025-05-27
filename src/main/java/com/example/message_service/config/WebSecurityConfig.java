package com.example.message_service.config;


import com.example.message_service.infrastructure.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests

                        .requestMatchers(
                                String.format("%s/auth/register", apiPrefix),
                                String.format("%s/auth/login", apiPrefix),
                                String.format("%s/auth/login-google", apiPrefix),
                                String.format("%s/auth/otp/send", apiPrefix),
                                String.format("%s/auth/otp/verify", apiPrefix),
                                String.format("%s/auth/reset-password", apiPrefix),
                                String.format("%s/auth/decode", apiPrefix)


                        ).permitAll()

                        .requestMatchers(
                                String.format("%s/users", apiPrefix),
                                String.format("%s/users/add", apiPrefix),
                                String.format("%s/users/delete/**", apiPrefix),
                                String.format("%s/users/getAllUser", apiPrefix)
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                String.format("%s/users/getByIdUser/**", apiPrefix),
                                String.format("%s/users/updateUser", apiPrefix),
                                String.format("%s/users/updateImg", apiPrefix),
                                String.format("%s/users/getImg/**", apiPrefix)
                        ).hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        config.setExposedHeaders(List.of("Authorization", "x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
