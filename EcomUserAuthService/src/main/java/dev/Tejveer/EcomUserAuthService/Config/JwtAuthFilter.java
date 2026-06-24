package dev.Tejveer.EcomUserAuthService.Config;

import dev.Tejveer.EcomUserAuthService.Entity.User;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Service.Implementation.CustomerDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomerDetailService customerDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        try {
            if (authHeader != null && !authHeader.startsWith("Bearer")){
                token = authHeader.split("Bearer ")[1];
                userId = jwtUtils.extractUserId(token);
                User user = customerDetailService.getUserById(UUID.fromString(userId));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ResourceNotFoundException e) {
            log.info("User credentials not matched");
        }

        filterChain.doFilter(request, response);

    }
}
