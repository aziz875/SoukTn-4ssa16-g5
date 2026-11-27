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
import tn.souktn.payment.entity.Refund;
import tn.souktn.payment.entity.RefundStatus;
import tn.souktn.payment.service.RefundService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Tag(name = "Refund Management", description = "Endpoints for managing refunds, including partial and full refunds linked to disputes")
public class RefundController {

    private final RefundService service;

    @GetMapping
    @Operation(
        summary = "Get all refunds",
        description = "Retrieve all refunds in the system. This should be called first to see all available refunds."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of refunds retrieved successfully",
            content = @Content(schema = @Schema(implementation = Refund[].class)))
    })
    public List<Refund> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get refund by ID",
        description = "Retrieve a specific refund using its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Refund found and returned"),
        @ApiResponse(responseCode = "404", description = "Refund not found with given ID")
    })
    public Refund findById(
        @Parameter(description = "The unique identifier of the refund", required = true)
        @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @GetMapping("/dispute/{disputeId}")
    @Operation(
        summary = "Get refunds by dispute ID",
        description = "Retrieve all refunds associated with a specific dispute (a dispute may have multiple refund attempts)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of refunds for the dispute",
            content = @Content(schema = @Schema(implementation = Refund[].class)))
    })
    public List<Refund> findByDisputeId(
        @Parameter(description = "The dispute ID from Dispute Service", required = true)
        @PathVariable Long disputeId
    ) {
        return service.findByDisputeId(disputeId);
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get refunds by status",
        description = "Retrieve all refunds with a specific status (PENDING, APPROVED, REJECTED, COMPLETED)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of refunds with given status",
            content = @Content(schema = @Schema(implementation = Refund[].class)))
    })
    public List<Refund> findByStatus(
        @Parameter(description = "Refund status filter (PENDING, APPROVED, REJECTED, COMPLETED)", required = true)
        @PathVariable RefundStatus status
    ) {
        return service.findByStatus(status);
    }

    @PostMapping
    @Operation(
        summary = "Create a new refund",
        description = "Create a new refund request, usually triggered by a dispute resolution. Status defaults to PENDING."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Refund created successfully",
            content = @Content(schema = @Schema(implementation = Refund.class))),
        @ApiResponse(responseCode = "400", description = "Invalid refund data (amount must be positive)")
    })
    public ResponseEntity<Refund> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refund object with amount and optional reason",
            required = true,
            content = @Content(schema = @Schema(implementation = Refund.class))
        )
        @Valid @RequestBody Refund refund
    ) {
        Refund created = service.create(refund);
        return ResponseEntity.created(URI.create("/api/refunds/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update refund details",
        description = "Update refund amount and reason. Cannot be updated once completed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Refund updated successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid refund data")
    })
    public Refund update(
        @Parameter(description = "The ID of the refund to update", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated refund data",
            required = true,
            content = @Content(schema = @Schema(implementation = Refund.class))
        )
        @Valid @RequestBody Refund refund
    ) {
        return service.update(id, refund);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update refund status",
        description = "Change the refund status (PENDING → APPROVED → COMPLETED or REJECTED)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Refund status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public Refund updateStatus(
        @Parameter(description = "The ID of the refund to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "New refund status (PENDING, APPROVED, REJECTED, COMPLETED)", required = true)
        @RequestParam RefundStatus status
    ) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete refund",
        description = "Delete a refund record (typically only for PENDING or REJECTED refunds)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Refund deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found with given ID")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "The ID of the refund to delete", required = true)
        @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
