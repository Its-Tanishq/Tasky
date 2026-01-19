package com.tasky.tasky.controller;

import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return new ResponseEntity<>("No refresh cookies found", HttpStatus.UNAUTHORIZED);
        }

        Optional<String> maybeRefreshToken = Optional.empty();

        for (Cookie c : cookies) {
            if (c.getName().equals("refreshToken")) {
                maybeRefreshToken = Optional.of(c.getValue());
            }
        }

        if (maybeRefreshToken.isEmpty()) {
            return new ResponseEntity<>("No refresh token found", HttpStatus.UNAUTHORIZED);
        }

        String refreshToken = maybeRefreshToken.get();
        
        if (!jwtUtil.validateToken(refreshToken)) {
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        if (!refreshTokenService.isValidRefreshToken(refreshToken)) {
            return new ResponseEntity<>("Refresh token not found or expired", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);

        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("refreshToken")) {
                    String refreshToken = c.getValue();
                    String email = jwtUtil.getEmailFromToken(refreshToken);
                    refreshTokenService.removeToken(email);

                    // Delete the cookie by setting its max age to 0
                    Cookie deleteCookie = new Cookie("refreshToken", null);
                    deleteCookie.setPath("/");
                    deleteCookie.setHttpOnly(true);
                    deleteCookie.setSecure(true);
                    deleteCookie.setMaxAge(0);
                    res.addCookie(deleteCookie);
                }
            }
        }

        return ResponseEntity.ok("Logged out successfully");
    }

}
