package com.example.work.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.expire}")
    private long expire;

    @Value("${jwt.merchant-secret}")
    private String merchantSecret;

    @Value("${jwt.admin-secret}")
    private String adminSecret;

    private Key merchantKey;
    private Key adminKey;


    @PostConstruct
    public void init() {
        merchantKey = Keys.hmacShaKeyFor(merchantSecret.getBytes(StandardCharsets.UTF_8));
        adminKey = Keys.hmacShaKeyFor(adminSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateMerchantToken(String merchantId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setSubject(username)
                .claim("merchantId", merchantId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(merchantKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAdminToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(adminKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generatePermanentMerchantToken(String merchantId, String username) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(username)
                .claim("merchantId", merchantId)
                .setIssuedAt(now)
                // 永久 Token 不设置过期时间
                .signWith(merchantKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseMerchantToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(merchantKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseAdminToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(adminKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateMerchantToken(String token) {
        try {
            parseMerchantToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean validateAdminToken(String token) {
        try {
            parseAdminToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getMerchantIdFromToken(String token) {
        Claims claims = parseMerchantToken(token);
        return claims.get("merchantId", String.class);
    }

    public String getAdminRoleFromToken(String token) {
        Claims claims = parseAdminToken(token);
        return claims.get("role", String.class);
    }
}
