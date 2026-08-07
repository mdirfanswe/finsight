package com.cashlens.expensetracker.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // A claim is a piece of information stored inside a JWT — like a key–value pair
        // that describes the user or token.
        // Every JWT is basically just a JSON object containing claims.
        // Claims basically contain the payload.
        return createToken(claims, username);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        String token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact(); // compact finalizes and encodes the JWT

        // .build() — returns an intermediate JWT object (not a String)
        // If you just call .build(), you get a JwtBuilder-built object (an internal
        // representation).
        // It’s not yet a token string.
        // Thus we use compact to get the jwt string

        return token;
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Boolean validateToken(String token, String usernameFromDB) {
        String extractedUsername = extractUsername(token);
        return (usernameFromDB.equals(extractedUsername) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}
