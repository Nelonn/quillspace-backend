package me.nelonn.quillspace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.nelonn.quillspace.dto.ErrorResponse;
import me.nelonn.quillspace.dto.RestError;
import me.nelonn.quillspace.model.Session;
import me.nelonn.quillspace.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtDecoder jwtDecoder;
    private final SessionRepository sessionRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, SessionRepository sessionRepository) {
        this.jwtDecoder = jwtDecoder;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(authHeader);
        } catch (JwtException e) {
            sendUnauthorized(response);
            return;
        }
        String subject = jwt.getSubject();
        if (!StringUtils.hasText(authHeader)) {
            sendUnauthorized(response);
            return;
        }
        Optional<Session> session = sessionRepository.findById(subject);
        if (session.isEmpty()) {
            sendUnauthorized(response);
            return;
        }
        SessionAuthentication sessionAuthentication = new SessionAuthentication(session.get());
        SecurityContextHolder.getContext().setAuthentication(sessionAuthentication);
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(RestError.UNAUTHORIZED.name()));
    }
}
