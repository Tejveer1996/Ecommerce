package dev.Tejveer.EcomPaymentService.Config.security;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private static final String WEBHOOK_PATH = "/apis/payment/webhook";

    /**
     * Fetch the token from httpResponse and fetch the claims after validating the token,
     * the token contains the userId as subject and email and roles as other claims
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (WEBHOOK_PATH.equals(request.getRequestURI())) {
            handleWebhookRequest(request, response, filterChain);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                token = authHeader.split("Bearer ")[1];
                Claims claims = jwtUtils.extractAllClaims(token);
                List<String> roles = claims.get("roles", List.class);
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities);
                authenticationToken.setDetails(claims.get("email"));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.info("User credentials not matched");
        }
        log.info(
                "Request: {} {}, Authentication: {}",
                request.getMethod(),
                request.getRequestURI(),
                SecurityContextHolder.getContext().getAuthentication()
        );
        filterChain.doFilter(request, response);

    }

    /**
     * Verifies HMAC-SHA256 signature (X-Razorpay-Signature) over the raw body,
     * using the webhook secret configured in the Razorpay Dashboard.
     */
    private void handleWebhookRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        String payload = cachedRequest.getCachedBodyAsString();
        String signature = request.getHeader("X-Razorpay-Signature");

        boolean isValid;
        try {
            isValid = signature != null && Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            log.info("Webhook signature verification failed: {}", e.getMessage());
            isValid = false;
        }

        if (!isValid) {
            log.info("Rejected webhook request: invalid or missing X-Razorpay-Signature");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                "razorpay-webhook", null, Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_SERVICE")
        ));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(cachedRequest, response);
    }
}
