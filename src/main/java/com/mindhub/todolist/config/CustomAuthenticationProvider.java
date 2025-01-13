package com.mindhub.todolist.config;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;/*
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.AuthenticationProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Custom authentication logic
        String username = authentication.getName();
        // userDetailsService: user's details (username: how I identify the user, password: 1234->hashed and comparison in the db)
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null) {
            // Perform additional authentication checks if needed
            return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        }
        throw new UsernameNotFoundException("User not found");
        // throw direct "Bad credentials" to not give clues to the attacker
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}*/
