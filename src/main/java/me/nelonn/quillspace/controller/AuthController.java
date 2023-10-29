package me.nelonn.quillspace.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.nelonn.quillspace.dto.Response;
import me.nelonn.quillspace.dto.RestError;
import me.nelonn.quillspace.model.Session;
import me.nelonn.quillspace.model.User;
import me.nelonn.quillspace.model.Views;
import me.nelonn.quillspace.repository.SessionRepository;
import me.nelonn.quillspace.repository.UserRepository;
import me.nelonn.quillspace.security.SessionAuthentication;
import me.nelonn.quillspace.service.ValidatorService;
import me.nelonn.quillspace.util.ErrorEntity;
import me.nelonn.quillspace.util.HttpUtils;
import me.nelonn.quillspace.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ValidatorService validatorService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Autowired
    public AuthController(UserRepository userRepository,
                          SessionRepository sessionRepository,
                          ValidatorService validatorService,
                          PasswordEncoder passwordEncoder,
                          JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.validatorService = validatorService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SignInUpPayload {
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class TokenDTO {
        private String token;
    }

    @PostMapping("/signUp")
    @JsonView(Views.Private.class)
    public ResponseEntity<Response> signUp(@RequestBody SignInUpPayload in, HttpServletRequest request) {
        String email = in.getEmail();
        RestError err = validatorService.validateEmail(email);
        if (err != null) {
            return new ErrorEntity(err);
        }
        err = validatorService.validatePassword(in.getPassword());
        if (err != null) {
            return new ErrorEntity(err);
        }
        if (userRepository.existsByEmail(email)) {
            return new ErrorEntity(RestError.EMAIL_OCCUPIED);
        }
        User user = User.builder().email(email).name("Anonymous").encryptedPassword(passwordEncoder.encode(in.getPassword())).build();
        user = userRepository.save(user);
        String token = createSessionToken(user, request);
        return new ResultEntity<>(new TokenDTO(token));
    }

    @PostMapping("/signIn")
    @JsonView(Views.Private.class)
    public ResponseEntity<Response> signIn(@RequestBody SignInUpPayload in, HttpServletRequest request) {
        String email = in.getEmail();
        RestError err = validatorService.validateEmail(email);
        if (err != null) {
            return new ErrorEntity(err);
        }
        User user = userRepository.findByEmail(in.getEmail()).orElse(null);
        if (user == null) {
            return new ErrorEntity(RestError.EMAIL_NOT_FOUND);
        }
        String token = createSessionToken(user, request);
        return new ResultEntity<>(new TokenDTO(token));
    }

    @PostMapping("/logOut")
    public ResponseEntity<Response> logOut() {
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        sessionRepository.deleteById(authentication.getSession().getId());
        return new ResultEntity<>(null);
    }

    private String createSessionToken(User user, HttpServletRequest request) {
        Session session = Session.builder()
                .userId(user.getId())
                .ip(HttpUtils.getRequestIP(request)).build();
        session = sessionRepository.save(session);
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .subject(session.getId())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
