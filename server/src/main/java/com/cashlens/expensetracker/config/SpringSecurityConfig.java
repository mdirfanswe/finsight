package com.cashlens.expensetracker.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cashlens.expensetracker.filter.JwtFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow CORS preflight requests
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated())
                // .formLogin(Customizer.withDefaults())
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.invalidateHttpSession(true);
                    logout.deleteCookies("JSESSIONID");
                    logout.logoutSuccessHandler((request, response, authentication) -> {
                        if (authentication == null || !authentication.isAuthenticated()) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                    });
                })
                // .httpBasic(Customizer.withDefaults())
                // .sessionManagement(session ->
                // session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // use the below code for session management when using JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Auto injects our userDetailsService and passwordEncoder as they are
        // declared as beans
        // It builds an AuthenticationManager pre-configured with your beans.
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://cashlens-three.vercel.app", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource is a Spring class
        // that maps URL patterns (like /api/**, /users/**, etc.) to specific CORS
        // configurations.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
