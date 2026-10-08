package com.secureorder.adapter.in.web;

import com.secureorder.application.PortsIn;
import com.secureorder.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication endpoints.
 * This belongs to the adapter.in.web layer.
 * Handles login, refresh, and logout endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final PortsIn portsIn;

    public AuthController(PortsIn portsIn) {
        this.portsIn = portsIn;
    }

    @Operation(
            summary = "Login user",
            description = "Authenticate a user and return access token with refresh token cookie",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Username of the user", required = true)
            @RequestParam String username,
            @Parameter(description = "Password of the user", required = true)
            @RequestParam String password) {
        try {
            String accessToken = portsIn.authenticate(username, password);
            
            // In a real implementation, we would also generate and set a refresh token cookie
            // For now, we'll return the access token
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("expiresIn", 300); // 5 minutes in seconds
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generate new access token using refresh token from cookie",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        try {
            String newAccessToken = portsIn.refresh(refreshToken);
            
            // In a real implementation, we would also generate and set a new refresh token cookie here
            // For now, we'll just return the new access token
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("expiresIn", 300); // 5 minutes in seconds
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Logout user",
            description = "Revoke refresh token family and clear cookie",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        try {
            portsIn.logout(refreshToken);
            // In a real implementation, we would also clear the cookie here by setting it to expire
            // For now, we just return success
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}