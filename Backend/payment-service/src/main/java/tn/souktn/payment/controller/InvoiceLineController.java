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
import tn.souktn.payment.entity.InvoiceLine;
import tn.souktn.payment.service.InvoiceLineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-lines")
@RequiredArgsConstructor
@Tag(name = "Invoice Line Management", description = "Endpoints for managing invoice lines (line items within invoices)")
public class InvoiceLineController {

    private final InvoiceLineService service;

    @GetMapping
    @Operation(
        summary = "Get all invoice lines",
        description = "Retrieve all invoice lines from all invoices. This should be called first to see all available invoice lines."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of invoice lines retrieved successfully",
            content = @Content(schema = @Schema(implementation = InvoiceLine[].class)))
    })
    public List<InvoiceLine> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get invoice line by ID",
        description = "Retrieve a specific invoice line using its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice line found and returned"),
        @ApiResponse(responseCode = "404", description = "Invoice line not found with given ID")
    })
    public InvoiceLine findById(
        @Parameter(description = "The unique identifier of the invoice line", required = true)
        @PathVariable Long id
    ) {
        return service.findById(id);
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get invoice lines by product ID",
        description = "Retrieve all invoice lines that contain a specific product (useful for checking sales history of a product)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of invoice lines with the product",
            content = @Content(schema = @Schema(implementation = InvoiceLine[].class)))
    })
    public List<InvoiceLine> findByProductId(
        @Parameter(description = "The product ID from Catalog Service", required = true)
        @PathVariable Long productId
    ) {
        return service.findByProductId(productId);
    }

    @GetMapping("/shop/{shopId}")
    @Operation(
        summary = "Get invoice lines by shop ID",
        description = "Retrieve all invoice lines from a specific shop/vendor"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of invoice lines from the shop",
            content = @Content(schema = @Schema(implementation = InvoiceLine[].class)))
    })
    public List<InvoiceLine> findByShopId(
        @Parameter(description = "The shop ID from User Service", required = true)
        @PathVariable Long shopId
    ) {
        return service.findByShopId(shopId);
    }

    @PostMapping
    @Operation(
        summary = "Create a new invoice line",
        description = "Add a new line item to an invoice. Contains product reference, quantity, and unit price."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Invoice line created successfully",
            content = @Content(schema = @Schema(implementation = InvoiceLine.class))),
        @ApiResponse(responseCode = "400", description = "Invalid invoice line data (missing required fields or validation errors)")
    })
    public ResponseEntity<InvoiceLine> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Invoice line object with product details and quantity",
            required = true,
            content = @Content(schema = @Schema(implementation = InvoiceLine.class))
        )
        @Valid @RequestBody InvoiceLine invoiceLine
    ) {
        InvoiceLine created = service.create(invoiceLine);
        return ResponseEntity.created(URI.create("/api/invoice-lines/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update invoice line details",
        description = "Update product name, unit price, and quantity. The product reference cannot be changed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice line updated successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice line not found with given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid invoice line data")
    })
    public InvoiceLine update(
        @Parameter(description = "The ID of the invoice line to update", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated invoice line data",
            required = true,
            content = @Content(schema = @Schema(implementation = InvoiceLine.class))
        )
        @Valid @RequestBody InvoiceLine invoiceLine
    ) {
        return service.update(id, invoiceLine);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete invoice line",
        description = "Remove a line item from an invoice"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Invoice line deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice line not found with given ID")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "The ID of the invoice line to delete", required = true)
        @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
