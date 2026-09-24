package com.cashlens.expensetracker.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "01234567890123456789012345678901");
    }

    @Test
    void generateToken_shouldCreateTokenFromUsername() {
        String token = jwtUtil.generateToken("alice");

        assertNotNull(token);
        assertEquals("alice", jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void createToken_shouldPreserveCustomClaimsAndSubject() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.createToken(claims, "alice");

        var payload = io.jsonwebtoken.Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "01234567890123456789012345678901".getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("alice", payload.getSubject());
        assertEquals("USER", payload.get("role"));
    }

    @Test
    void validateToken_shouldReturnTrueForMatchingNonExpiredUsername() {
        String token = jwtUtil.generateToken("alice");

        assertTrue(jwtUtil.validateToken(token, "alice"));
    }

    @Test
    void validateToken_shouldReturnFalseWhenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken("alice");

        assertFalse(jwtUtil.validateToken(token, "bob"));
    }
}
