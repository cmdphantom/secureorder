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
        // In a real implementation:
        // 1. Find user by username
        // 2. Check password using security adapter (via portsIn)
        // 3. If valid, generate access token and refresh token
        // 4. Set refresh token as HttpOnly cookie
        // 5. Return access token in response body
        
        // Placeholder implementation
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", "placeholder_access_token");
        response.put("expiresIn", 300); // 5 minutes in seconds
        
        // In reality, we would also set a cookie here for the refresh token
        // response.setHeader("Set-Cookie", "refreshToken=placeholder; HttpOnly; Secure; SameSite=Strict; Path=/api/auth");
        
        return ResponseEntity.ok(response);
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
        // In a real implementation:
        // 1. Extract refresh token from cookie
        // 2. Validate it using security adapter (via portsIn)
        // 3. If valid, generate new access token and refresh token (rotation)
        // 4. Set new refresh token as HttpOnly cookie
        // 5. Return new access token in response body
        
        // Placeholder implementation
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body("Refresh token required");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", "new_placeholder_access_token");
        response.put("expiresIn", 300); // 5 minutes in seconds
        
        return ResponseEntity.ok(response);
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
        // In a real implementation:
        // 1. Extract refresh token from cookie
        // 2. Revoke the refresh token family using security adapter (via portsIn)
        // 3. Clear the cookie by setting it to expire
        
        // Placeholder implementation
        return ResponseEntity.noContent().build();
    }
}