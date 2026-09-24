package com.cashlens.expensetracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldMapStoredUserToSpringUserDetails() {
        UserModel user = new UserModel();
        user.setUsername("alice");
        user.setPassword("encoded-password");
        user.setRole("USER");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("alice");

        assertEquals("alice", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(true, result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing"));
    }
}
