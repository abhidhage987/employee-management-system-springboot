package com.abhi.employeemanagement.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private JwtService jwtService;

    private CustomUserDetailsService
            customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,

            CustomUserDetailsService
                    customUserDetailsService) {

        this.jwtService = jwtService;

        this.customUserDetailsService =
                customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain)

            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        String token = null;

        String email = null;

        // CHECK TOKEN
        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            token =
                    authHeader.substring(7);

            email =
                    jwtService.extractUsername(
                            token
                    );
        }

        
        if (email != null
                &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        == null) {

            UserDetails userDetails =

                    customUserDetailsService
                            .loadUserByUsername(
                                    email
                            );

            if (jwtService.validateToken(
                    token,
                    userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken
                        authToken =

                        new UsernamePasswordAuthenticationToken(

                                userDetails,

                                null,

                                userDetails.getAuthorities()
                        );

                authToken.setDetails(

                        new WebAuthenticationDetailsSource()

                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}