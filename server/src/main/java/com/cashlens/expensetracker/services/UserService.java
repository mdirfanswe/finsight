package com.cashlens.expensetracker.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cashlens.expensetracker.DTO.EmailDetails;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.UserRepository;

import static com.cashlens.expensetracker.utils.constants.RegexConstants.PASSWORD_REGEX;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${password.reset.url}")
    private String PASSWORD_RESET_URL;

    @Value("${password.reset.expiryInMinutes}")
    private int PASSWORD_RESET_EXPIRY_IN_MINUTES;

    public UserModel save(UserModel user) {
        String rawPassword = user.getPassword();
        if (!rawPassword.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        // if (user.getRoles().isEmpty()) {
        // user.getRoles().add("USER");
        // }
        if (user.getRole() == null || user.getRole().trim().equals("")) {
            user.setRole("USER");
        }
        if (user.getCurrency() == null) {
            user.setCurrency("INR");
        }
        return userRepository.save(user);
    }

    public Optional<UserModel> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean delete(String username) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
        return true;
    }

    public boolean sendResetPasswordEmail(String email) {
        try {
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String passwordResetToken = UUID.randomUUID().toString();
            LocalDateTime passwordResetExpiry = LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_IN_MINUTES);
            user.setPasswordResetToken(passwordResetToken);
            user.setPasswordResetExpiry(passwordResetExpiry);
            userRepository.save(user);

            String body = "If you did not do this then please IGNORE this email.\n\n"
                    + "Click the link to reset your password: " + PASSWORD_RESET_URL + "?token=" + passwordResetToken
                    + "\n\nThis link will expire in" + PASSWORD_RESET_EXPIRY_IN_MINUTES + "minutes.";

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("Reset Password")
                    .msgBody(body)
                    .build();

            emailService.sendSimpleMail(emailDetails);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean resetPassword(String token, String password, String confirmPassword) {
        UserModel user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        LocalDateTime tokenExpiry = user.getPasswordResetExpiry();
        if (tokenExpiry.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);

        return true;
    }

}
