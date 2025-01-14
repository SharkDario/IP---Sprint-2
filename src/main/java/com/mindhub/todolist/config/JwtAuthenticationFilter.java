package com.mindhub.todolist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Filter for session

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService; // Authentication for the user

    // request, response and filter chain
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // URI after the localhost
        String requestURI = request.getRequestURI();

        // Allow access to public endpoints without authentication
        if (requestURI.startsWith("/public/")) {
            chain.doFilter(request, response);
            return;
        }
        // return the key of the session
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;
        // substring the "Bearer " to have the key
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try { // extract the username
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                logger.error("Error extracting username from token", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // user's details by the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // userDetails already user validated
            // validates the token with the details
            if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // user's authorities/rol
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // in this petition is the authenticate user
                SecurityContextHolder.getContext().setAuthentication(authentication); // when create a controller, can get the user's information (that is doing the petition) by the authentication
            }
        } // all this is going to happen before to get to the controller
        chain.doFilter(request, response);
    }
}
