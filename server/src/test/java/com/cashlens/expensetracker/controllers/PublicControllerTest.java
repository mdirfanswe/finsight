package com.cashlens.expensetracker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;

import com.cashlens.expensetracker.DTO.LoginRequestDTO;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.services.UserDetailsServiceImpl;
import com.cashlens.expensetracker.services.UserService;
import com.cashlens.expensetracker.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PublicController publicController;

    @Test
    void login_shouldRejectPasswordBeforeAuthenticationWhenFormatIsInvalid() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("alice");
        request.setPassword("weakpassword");

        var response = publicController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password does not meet complexity requirements.", response.getBody());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_shouldAuthenticateUserAndReturnJwt() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("alice");
        request.setPassword("Strong@123");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("alice"))
                .thenReturn(User.withUsername("alice").password("encoded").roles("USER").build());
        when(jwtUtil.generateToken("alice")).thenReturn("jwt-token");

        var response = publicController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("alice");
    }

    @Test
    void login_shouldReturnUnauthorizedWhenAuthenticationFails() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("alice");
        request.setPassword("Strong@123");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        var response = publicController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
        verify(jwtUtil, never()).generateToken(any());
    }
}
