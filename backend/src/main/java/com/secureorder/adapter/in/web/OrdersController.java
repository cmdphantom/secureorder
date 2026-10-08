package com.secureorder.adapter.in.web;

import com.secureorder.application.PortsIn;
import com.secureorder.domain.Order;
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

import java.util.List;

/**
 * REST Controller for order endpoints.
 * This belongs to the adapter.in.web layer.
 * Handles order creation, retrieval, approval, and rejection.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrdersController {

    private final PortsIn portsIn;

    public OrdersController(PortsIn portsIn) {
        this.portsIn = portsIn;
    }

    @Operation(
            summary = "Create a new order",
            description = "Create a new order (OPERATOR only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "Order reference ID", required = true)
            @RequestParam String reference,
            @Parameter(description = "Order amount (must be positive)", required = true)
            @RequestParam double amount,
            @Parameter(description = "Currency code (ISO 4217 format, e.g., USD, EUR)", required = true)
            @RequestParam String currency,
            @Parameter(description = "Counterparty name", required = true)
            @RequestParam String counterparty,
            @Parameter(description = "ID of the user creating the order", required = true)
            @RequestHeader("X-User-ID") Long createdBy) {
        // In a real implementation, we would get the user ID from the JWT token
        // For now, we're using a header as a placeholder
        
        Order order = portsIn.createOrder(reference, amount, currency, counterparty, createdBy);
        return ResponseEntity.status(201).body(order);
    }

    @Operation(
            summary = "Get current user's orders",
            description = "Retrieve all orders for the authenticated user (OPERATOR only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping("/mine")
    public ResponseEntity<List<Order>> getMyOrders(@RequestHeader("X-User-ID") Long userId) {
        // In a real implementation, we would get the user ID from the JWT token
        List<Order> orders = portsIn.getMyOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get pending orders",
            description = "Retrieve all orders with PENDING status (VALIDATOR only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @GetMapping
    public ResponseEntity<List<Order>> getPendingOrders(
            @Parameter(description = "Filter by status (use 'PENDING' to get pending orders)", required = false)
            @RequestParam(value = "status", required = false) String status) {
        // In a real implementation, we would check that the user has VALIDATOR role
        // For now, we're just returning pending orders if status=PENDING is requested
        
        if ("PENDING".equalsIgnoreCase(status)) {
            List<Order> orders = portsIn.getPendingOrders();
            return ResponseEntity.ok(orders);
        }
        
        // For other statuses or no status, we could return all orders or empty list
        // But per spec, VALIDATOR only sees PENDING orders
        return ResponseEntity.ok(List.of());
    }

    @Operation(
            summary = "Approve an order",
            description = "Approve a pending order (VALIDATOR only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order approved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be approved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Conflict - order already processed")
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<Order> approveOrder(
            @Parameter(description = "ID of the order to approve", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID of the user deciding on the order", required = true)
            @RequestHeader("X-User-ID") Long decidedBy) {
        // In a real implementation:
        // 1. Extract user ID from JWT token
        // 2. Validate that user has VALIDATOR role
        // 3. Call approveOrder use case
        
        Order order = portsIn.approveOrder(id, decidedBy);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Reject an order",
            description = "Reject a pending order with reason (VALIDATOR only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order rejected successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid rejection reason"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Conflict - order already processed")
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<Order> rejectOrder(
            @Parameter(description = "ID of the order to reject", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID of the user deciding on the order", required = true)
            @RequestHeader("X-User-ID") Long decidedBy,
            @Parameter(description = "Reason for rejection (required)", required = true)
            @RequestParam String decisionReason) {
        // In a real implementation:
        // 1. Extract user ID from JWT token
        // 2. Validate that user has VALIDATOR role
        // 3. Validate that decisionReason is not empty
        // 4. Call rejectOrder use case
        
        Order order = portsIn.rejectOrder(id, decidedBy, decisionReason);
        return ResponseEntity.ok(order);
    }
}