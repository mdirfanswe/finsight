package com.cashlens.expensetracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.cashlens.expensetracker.DTO.EmailDetails;
import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "PASSWORD_RESET_URL", "https://cashlens.example/reset-password");
        ReflectionTestUtils.setField(userService, "PASSWORD_RESET_EXPIRY_IN_MINUTES", 15);
        user = new UserModel();
        user.setId("user-1");
        user.setUsername("alice");
        user.setEmail("alice@example.com");
    }

    @Test
    void save_shouldRejectPasswordThatDoesNotMeetConfiguredRule() {
        user.setPassword("weakpassword");

        assertThrows(IllegalArgumentException.class, () -> userService.save(user));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void save_shouldEncodePasswordAndApplyRoleAndCurrencyDefaults() {
        user.setPassword("Strong@123");
        user.setRole("  ");
        user.setCurrency(null);
        when(passwordEncoder.encode("Strong@123")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        UserModel result = userService.save(user);

        assertSame(user, result);
        assertEquals("encoded-password", user.getPassword());
        assertEquals("USER", user.getRole());
        assertEquals("INR", user.getCurrency());
        verify(passwordEncoder).encode("Strong@123");
        verify(userRepository).save(user);
    }

    @Test
    void delete_shouldDeleteFoundUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertEquals(true, userService.delete("alice"));
        verify(userRepository).delete(user);
    }

    @Test
    void delete_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.delete("alice"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void sendResetPasswordEmail_shouldSaveTokenAndSendMail() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(true, userService.sendResetPasswordEmail("alice@example.com"));

        verify(userRepository).save(user);
        verify(emailService).sendSimpleMail(any(EmailDetails.class));
        assertEquals("alice@example.com", user.getEmail());
        org.junit.jupiter.api.Assertions.assertNotNull(user.getPasswordResetToken());
        org.junit.jupiter.api.Assertions.assertNotNull(user.getPasswordResetExpiry());
        assertEquals(true, user.getPasswordResetExpiry().isAfter(LocalDateTime.now()));

        ArgumentCaptor<EmailDetails> captor = ArgumentCaptor.forClass(EmailDetails.class);
        verify(emailService).sendSimpleMail(captor.capture());
        EmailDetails details = captor.getValue();
        assertEquals("alice@example.com", details.getRecipient());
        assertEquals("Reset Password", details.getSubject());
        assertEquals(true, details.getMsgBody().contains(user.getPasswordResetToken()));
        assertEquals(true, details.getMsgBody().contains("https://cashlens.example/reset-password?token="));
    }

    @Test
    void sendResetPasswordEmail_shouldReturnFalseWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertEquals(false, userService.sendResetPasswordEmail("missing@example.com"));
        verify(emailService, never()).sendSimpleMail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void sendResetPasswordEmail_shouldReturnFalseWhenMailSendingFails() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(emailService.sendSimpleMail(any(EmailDetails.class))).thenThrow(new RuntimeException("mail failure"));

        assertEquals(false, userService.sendResetPasswordEmail("alice@example.com"));
        verify(userRepository).save(user);
        verify(emailService).sendSimpleMail(any(EmailDetails.class));
    }

    @Test
    void resetPassword_shouldRejectWeakPasswordBeforeUpdatingUser() {
        user.setPasswordResetToken("token-1");
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByPasswordResetToken("token-1")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("token-1", "weakpassword", "weakpassword"));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_shouldRejectMismatchedPasswords() {
        user.setPasswordResetToken("token-1");
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByPasswordResetToken("token-1")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("token-1", "Strong@123", "Different@123"));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_shouldRejectExpiredToken() {
        user.setPasswordResetToken("token-1");
        user.setPasswordResetExpiry(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByPasswordResetToken("token-1")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("token-1", "Strong@123", "Strong@123"));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_shouldEncodeNewPasswordAndClearResetState() {
        user.setPassword("Old@1234");
        user.setPasswordResetToken("token-1");
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByPasswordResetToken("token-1")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("Strong@123")).thenReturn("encoded-new-password");
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(true, userService.resetPassword("token-1", "Strong@123", "Strong@123"));

        assertEquals("encoded-new-password", user.getPassword());
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetExpiry());
        verify(passwordEncoder).encode("Strong@123");
        verify(userRepository).save(user);
    }
}
