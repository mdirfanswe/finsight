package com.cashlens.expensetracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cashlens.expensetracker.DTO.LoginRequestDTO;
import com.cashlens.expensetracker.DTO.ResetPasswordDTO;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.services.UserDetailsServiceImpl;
import com.cashlens.expensetracker.services.UserService;
import com.cashlens.expensetracker.utils.JwtUtil;

import static com.cashlens.expensetracker.utils.constants.RegexConstants.PASSWORD_REGEX;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("public")
@Slf4j
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@RequestBody UserModel user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        try {
            String rawPassword = loginRequest.getPassword();
            if (!rawPassword.matches(PASSWORD_REGEX)) {
                throw new IllegalArgumentException(
                        "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
            }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return new ResponseEntity<>(jwt, HttpStatus.OK);

            // // Get or create the session
            // HttpSession session = request.getSession(true);

            // // Store authentication in session so Spring can manage it
            // session.setAttribute(
            // "SPRING_SECURITY_CONTEXT",
            // SecurityContextHolder.getContext());

            // // Set authentication in SecurityContextHolder
            // SecurityContextHolder.getContext().setAuthentication(auth);

            // return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occured while createAuthenticationToken: " + e);
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get-user/{username}")
    public ResponseEntity<UserModel> getUserByUsername(@PathVariable("username") String username) {
        UserModel user = userService.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isMailSent = userService.sendResetPasswordEmail(email);
        if (isMailSent) {
            return new ResponseEntity<>("Password reset email sent successfully!",
                    HttpStatus.OK);
        }

        return new ResponseEntity<>("Error while sending password reset email!",
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        String token = request.getToken();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        boolean isPasswordReset = userService.resetPassword(token, password, confirmPassword);
        if (isPasswordReset) {
            return new ResponseEntity<>("Password reset successfully!", HttpStatus.OK);
        }

        // TODO: Handle Error Responses better by modifying Services and more
        return new ResponseEntity<>("Error while resetting password!", HttpStatus.BAD_REQUEST);
    }
}
