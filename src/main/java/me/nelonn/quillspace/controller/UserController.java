package me.nelonn.quillspace.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.nelonn.quillspace.dto.Response;
import me.nelonn.quillspace.dto.RestError;
import me.nelonn.quillspace.model.User;
import me.nelonn.quillspace.model.Views;
import me.nelonn.quillspace.repository.UserRepository;
import me.nelonn.quillspace.security.SessionAuthentication;
import me.nelonn.quillspace.util.ErrorEntity;
import me.nelonn.quillspace.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    @JsonView(Views.Private.class)
    public ResponseEntity<Response> getMe() {
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findById(authentication.getSession().getUserId());
        if (userOptional.isEmpty()) {
            return new ErrorEntity(RestError.INTERNAL_SERVER_ERROR);
        }
        return new ResultEntity<>(userOptional.get());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdatePayload {
        private String name;
        private String avatar;
        private String password;
        @JsonProperty("old_password")
        private String oldPassword;

    }

    @PatchMapping("/me")
    public ResponseEntity<Response> updateMe(@RequestBody UpdatePayload updatePayload) {
        if (StringUtils.hasText(updatePayload.getPassword()) && !StringUtils.hasText(updatePayload.getOldPassword())) {
            return new ErrorEntity(RestError.OLD_PASSWORD_IS_EMPTY);
        }
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findById(authentication.getSession().getUserId());
        if (userOptional.isEmpty()) {
            return new ErrorEntity(RestError.INTERNAL_SERVER_ERROR);
        }
        User user = userOptional.get();
        if (StringUtils.hasText(updatePayload.getPassword())) {
            String oldPassword = passwordEncoder.encode(updatePayload.getOldPassword());
            if (!oldPassword.equals(user.getEncryptedPassword())) {
                return new ErrorEntity(RestError.OLD_PASSWORD_NOT_MATCHES);
            }
            user.setEncryptedPassword(passwordEncoder.encode(updatePayload.getPassword()));
        }
        if (StringUtils.hasText(updatePayload.getName())) {
            user.setName(updatePayload.getName());
        }
        // TODO: avatar updating
        return new ResultEntity<>(null);
    }


}
