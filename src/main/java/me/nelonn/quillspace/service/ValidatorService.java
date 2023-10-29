package me.nelonn.quillspace.service;

import me.nelonn.quillspace.dto.RestError;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class ValidatorService {
    private static final Pattern VALID_EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    public RestError validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return RestError.EMAIL_IS_EMPTY;
        }
        return email.length() < 6 ||
                email.length() > 254 || // RFC 2821
                !VALID_EMAIL.matcher(email).matches() ?
                RestError.EMAIL_INVALID : null;
    }

    public RestError validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            return RestError.PASSWORD_IS_EMPTY;
        }
        if (password.length() < 8) {
            return RestError.PASSWORD_TOO_SHORT;
        }
        if (password.length() > 128) {
            return RestError.PASSWORD_TOO_LONG;
        }
        int digits = 0, lowChars = 0, upChars = 0, specChars = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isLowerCase(c)) {
                lowChars++;
            } else if (Character.isUpperCase(c)) {
                upChars++;
            } else {
                specChars++;
            }
        }
        if (digits == 0) {
            return RestError.PASSWORD_REQUIRED_ONE_DIGIT;
        }
        if (lowChars == 0) {
            return RestError.PASSWORD_REQUIRED_ONE_LOWERCASE_CHAR;
        }
        if (upChars == 0) {
            return RestError.PASSWORD_REQUIRED_ONE_UPPERCASE_CHAR;
        }
        if (specChars == 0) {
            return RestError.PASSWORD_REQUIRED_ONE_SPECIAL_CHAR;
        }
        return null;
    }
}
