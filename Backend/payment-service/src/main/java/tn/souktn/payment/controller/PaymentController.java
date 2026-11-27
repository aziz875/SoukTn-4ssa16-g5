package tn.souktn.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.souktn.payment.entity.Payment;
import tn.souktn.payment.entity.PaymentStatus;
import tn.souktn.payment.service.PaymentService;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for managing payments, including creation, retrieval, updates, and deletion")
public class PaymentController {

    private final PaymentService service;

    @GetMapping
    @Operation(
        summary = "Get all payments",
        description = "Retrieve all payments, optionally filtered by userId. This should be called first to see all available payments."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of payments retrieved successfully",
            content = @Content(schema = @Schema(implementation = Payment[].class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public List<Payment> findAll(
        @Parameter(description = "Optional: Filter payments by userId (Keycloak user ID)")
        @RequestParam(required = false) String userId
    ) {
        return service.findAll(userId);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieve a specific payment using its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment found and returned"),
        @ApiResponse(responseCode = "404", description = "Payment not found with given ID")
    })
    public Payment findById(
        @Parameter(description = "The unique identifier of the payment", required = true)
        @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @GetMapping("/order/{orderId}")
    @Operation(
        summary = "Get payment by order ID",
        description = "Retrieve the payment associated with a specific order. Each order has at most one payment."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment found and returned"),
        @ApiResponse(responseCode = "404", description = "No payment found for the given order ID")
    })
    public Payment findByOrderId(
        @Parameter(description = "The order ID to search for (from Order Service)", required = true)
        @PathVariable Long orderId
    ) {
        return service.findByOrderId(orderId);
    }

    @PostMapping
    @Operation(
        summary = "Create a new payment",
        description = "Create a new payment record. The payment will be automatically associated with an invoice if one is provided. Status defaults to PENDING."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment created successfully",
            content = @Content(schema = @Schema(implementation = Payment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid payment data (missing required fields or validation errors)"),
        @ApiResponse(responseCode = "409", description = "A payment already exists for the given order ID")
    })
    public ResponseEntity<Payment> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payment object to be created",
            required = true,
            content = @Content(schema = @Schema(implementation = Payment.class))
        )
        @Valid @RequestBody Payment payment
    ) {
        Payment created = service.create(payment);
        return ResponseEntity.created(URI.create("/api/payments/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update payment details",
        description = "Update payment information (amount, currency, payment method). Cannot be updated if already processed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid payment data")
    })
    public Payment update(
        @Parameter(description = "The ID of the payment to update", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated payment data",
            required = true,
            content = @Content(schema = @Schema(implementation = Payment.class))
        )
        @Valid @RequestBody Payment payment
    ) {
        return service.update(id, payment);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update payment status",
        description = "Change the payment status (PENDING → COMPLETED, FAILED, REFUNDED, etc.)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public Payment updateStatus(
        @Parameter(description = "The ID of the payment to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "New payment status (PENDING, COMPLETED, FAILED, REFUNDED, PARTIAL_REFUND)", required = true)
        @RequestParam PaymentStatus status
    ) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete payment",
        description = "Delete a payment record and all associated invoices and refunds (cascade delete)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found with given ID")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "The ID of the payment to delete", required = true)
        @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}