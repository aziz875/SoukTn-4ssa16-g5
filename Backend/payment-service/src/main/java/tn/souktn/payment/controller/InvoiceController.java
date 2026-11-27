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
import tn.souktn.payment.entity.Invoice;
import tn.souktn.payment.entity.InvoiceStatus;
import tn.souktn.payment.service.InvoiceService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "Endpoints for managing invoices, including creation, retrieval, status updates, and deletion")
public class InvoiceController {

    private final InvoiceService service;

    @GetMapping
    @Operation(
        summary = "Get all invoices",
        description = "Retrieve all invoices. This should be called first to see all available invoices in the system."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of invoices retrieved successfully",
            content = @Content(schema = @Schema(implementation = Invoice[].class)))
    })
    public List<Invoice> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get invoice by ID",
        description = "Retrieve a specific invoice using its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice found and returned"),
        @ApiResponse(responseCode = "404", description = "Invoice not found with given ID")
    })
    public Invoice findById(
        @Parameter(description = "The unique identifier of the invoice", required = true)
        @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(
        summary = "Get invoice by invoice number",
        description = "Retrieve a specific invoice using its unique invoice number (e.g., INV-ABC123XY)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice found and returned"),
        @ApiResponse(responseCode = "404", description = "Invoice not found with given invoice number")
    })
    public Invoice findByInvoiceNumber(
        @Parameter(description = "The invoice number to search for (format: INV-xxxxxxxx)", required = true)
        @PathVariable String invoiceNumber
    ) {
        return service.findByInvoiceNumber(invoiceNumber);
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get invoices by status",
        description = "Retrieve all invoices with a specific status (DRAFT, ISSUED, PAID, CANCELLED)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of invoices with given status",
            content = @Content(schema = @Schema(implementation = Invoice[].class)))
    })
    public List<Invoice> findByStatus(
        @Parameter(description = "Invoice status filter (DRAFT, ISSUED, PAID, CANCELLED)", required = true)
        @PathVariable InvoiceStatus status
    ) {
        return service.findByStatus(status);
    }

    @PostMapping
    @Operation(
        summary = "Create a new invoice",
        description = "Create a new invoice with associated invoice lines. Status defaults to DRAFT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Invoice created successfully",
            content = @Content(schema = @Schema(implementation = Invoice.class))),
        @ApiResponse(responseCode = "400", description = "Invalid invoice data"),
        @ApiResponse(responseCode = "409", description = "Invoice number already exists")
    })
    public ResponseEntity<Invoice> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Invoice object with associated invoice lines",
            required = true,
            content = @Content(schema = @Schema(implementation = Invoice.class))
        )
        @Valid @RequestBody Invoice invoice
    ) {
        Invoice created = service.create(invoice);
        return ResponseEntity.created(URI.create("/api/invoices/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update invoice details",
        description = "Update invoice information and its associated lines"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice updated successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid invoice data")
    })
    public Invoice update(
        @Parameter(description = "The ID of the invoice to update", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated invoice data",
            required = true,
            content = @Content(schema = @Schema(implementation = Invoice.class))
        )
        @Valid @RequestBody Invoice invoice
    ) {
        return service.update(id, invoice);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update invoice status",
        description = "Change the invoice status (DRAFT → ISSUED → PAID). When changed to ISSUED, issuedAt timestamp is set automatically."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public Invoice updateStatus(
        @Parameter(description = "The ID of the invoice to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "New invoice status (DRAFT, ISSUED, PAID, CANCELLED)", required = true)
        @RequestParam InvoiceStatus status
    ) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete invoice",
        description = "Delete an invoice and all associated invoice lines (cascade delete)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Invoice deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found with given ID")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "The ID of the invoice to delete", required = true)
        @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
