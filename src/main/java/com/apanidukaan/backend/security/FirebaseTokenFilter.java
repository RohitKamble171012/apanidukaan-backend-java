package com.apanidukaan.backend.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);

            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

                FirebaseUserPrincipal principal =
                        new FirebaseUserPrincipal(decoded.getUid(), decoded.getEmail());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (FirebaseAuthException e) {
                // invalid/expired token — leave unauthenticated, let SecurityConfig decide what happens next
                System.out.println("Firebase token verification failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}