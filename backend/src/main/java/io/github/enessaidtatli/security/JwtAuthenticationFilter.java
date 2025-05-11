package io.github.enessaidtatli.security;

import io.github.enessaidtatli.util.JwtUtil;
import io.github.enessaidtatli.util.SecurityContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Log log = LogFactory.getLog(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authentication");
            String token = "";
            String email = "";
            if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            } else {
                token = header.substring(7);
                email = jwtUtil.extractUsername(token);
            }
            if (Objects.nonNull(email) && Objects.isNull(SecurityContextUtil.getAuthentication()))  {
              UserDetails userDetails = userDetailsService.loadUserByUsername(email);
              if(jwtUtil.validateToken(token, userDetails)){
                  UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                  authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                  SecurityContextHolder.getContext().setAuthentication(authToken);
              }
            }
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException exception) {
            log.error("");
            throw new AccessDeniedException("");
        }
    }
}
