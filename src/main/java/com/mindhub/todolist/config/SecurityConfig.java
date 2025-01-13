package com.mindhub.todolist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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

import java.util.Arrays;

// @Configuration indicate to spring boot inside the class SecurityConfig are going to be one or various Beans
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Define the password encoder bean
    }
    // Need a bean for being inside the spring's context, spring can create it and leave it ready at the start of the app
    @Bean // a new SecurityFilterChain with our rules in the security
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http //HttpSecurity we use different methods
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html" ,"/h2-console/**")
                                .permitAll() // to access to the API and the db when you are in development
                                .requestMatchers( "/api/auth/**", "/index.html" ).permitAll() // anyone can access to this routes
                                .requestMatchers("/api/user/**").hasAuthority("USER") // USER can access to these routes **: means all routes from there
                                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")// Allow public access to specific endpoints
                                .anyRequest().denyAll() // All other requests must be authenticated
                )
                .formLogin(AbstractHttpConfigurer::disable) // deactivated the form login
                .httpBasic(AbstractHttpConfigurer::disable) // deactivated the basic http config
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource()) // config the cors
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session policy: without state, the session doesn't exist in the server-side, only the token, info about the session
                ) // add the filter: jwtAuthenticationFilter, personalized
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Apply JWT filter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // from any frontend they can do petitions
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}