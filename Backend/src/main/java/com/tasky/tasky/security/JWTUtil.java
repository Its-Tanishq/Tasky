package com.tasky.tasky.security;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.repo.EmployeeRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    @Autowired
    @Lazy
    private EmployeeRepo employeeRepo;

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateAccessToken(String email) {
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        Map<String, Integer> claims = new HashMap<>();
        claims.put("orgId", Math.toIntExact(employee.getOrganization().getId()));
        claims.put("empId", Math.toIntExact(employee.getId()));
        claims.put("role", Math.toIntExact(employee.getRole().getId()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getOrgIdFromToken(String token) {
        return getClaimFromToken(token, "orgId");
    }
    public int getEmpIdFromToken(String token) {
        return getClaimFromToken(token, "empId");
    }
    public int getRoleIdFromToken(String token) {
        return getClaimFromToken(token, "role");
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private int getClaimFromToken(String token, String claimKey) {
        return (int) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey);
    }
}
