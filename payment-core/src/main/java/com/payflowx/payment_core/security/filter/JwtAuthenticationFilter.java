package com.payflowx.payment_core.security.filter;

import com.payflowx.payment_core.security.jwt.JwtService;
import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Read Aithorization Header
        String authHeader = request.getHeader("Authorization");

        //Check is Header Missing?
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Extract Token
        String token = authHeader.substring(7);


        //Validate Token
        if(!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }


        //Extract user ID
        UUID userId = jwtService.extractUserId(token);

        //Load User
        User user = userRepository.findById(userId).orElse(null);

        //Check is User Missing?
        if(user == null){
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
