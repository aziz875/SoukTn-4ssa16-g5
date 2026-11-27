# SoukTn API Implementation Guide
**Complete CRUD with Swagger for All Services**  
**Version:** 1.0  
**Status:** Phase 1 Complete (Payment Service) ✅  
**Last Updated:** 2026-11-27

---

## 🎯 Project Overview

This guide standardizes API implementation across all SoukTn microservices. Each service will have:

✅ **Complete CRUD operations** for all entities  
✅ **Comprehensive Swagger/OpenAPI documentation**  
✅ **Best-practice naming conventions**  
✅ **30+ testable endpoints** per service  
✅ **API reference documentation** (`.md` file)  

---

## 📊 Phase 1: Payment Service ✅ COMPLETE

### What Was Done

#### Repositories (3 New)
```
✅ InvoiceRepository       → Query invoices by number, status
✅ InvoiceLineRepository   → Query lines by productId, shopId
✅ RefundRepository        → Query refunds by disputeId, status
```

#### Services (3 New)
```
✅ InvoiceService          → 7 business methods
✅ InvoiceLineService      → 6 business methods
✅ RefundService           → 7 business methods
```

#### Controllers (4 Total)
```
✅ PaymentController       → 7 endpoints (enhanced)
✅ InvoiceController       → 8 endpoints
✅ InvoiceLineController   → 7 endpoints
✅ RefundController        → 8 endpoints
```

#### Endpoints: 30 Total
```
Payment:      GET all, GET by ID, GET by order, POST, PUT, PATCH status, DELETE
Invoice:      GET all, GET by ID, GET by number, GET by status, POST, PUT, PATCH status, DELETE
InvoiceLine:  GET all, GET by ID, GET by product, GET by shop, POST, PUT, DELETE
Refund:       GET all, GET by ID, GET by dispute, GET by status, POST, PUT, PATCH status, DELETE
```

#### Documentation (3 New)
```
✅ PAYMENT_API_DOCUMENTATION.md    → 500+ lines complete reference
✅ PAYMENT_SERVICE_COMPLETE.md     → Implementation summary
✅ PAYMENT_SERVICE_AUDIT.md        → Requirements checklist
```

### Quick Links
- 📚 **Full API Reference**: [PAYMENT_API_DOCUMENTATION.md](PAYMENT_API_DOCUMENTATION.md)
- ✨ **Implementation Details**: [PAYMENT_SERVICE_COMPLETE.md](PAYMENT_SERVICE_COMPLETE.md)
- ✅ **Verification Checklist**: [PAYMENT_SERVICE_AUDIT.md](PAYMENT_SERVICE_AUDIT.md)

---

## 🏗️ Implementation Pattern (For Other Services)

### Step 1: Create Repositories
```java
// Template: src/main/java/tn/souktn/[service]/repository/[Entity]Repository.java

package tn.souktn.[service].repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.[service].entity.[Entity];

public interface [Entity]Repository extends JpaRepository<[Entity], Long> {
    // Add custom query methods
    // Example: Optional<[Entity]> findBy[Field]([Type] value);
    // Example: List<[Entity]> findBy[Status]([StatusEnum] status);
}
```

### Step 2: Create Services
```java
// Template: src/main/java/tn/souktn/[service]/service/[Entity]Service.java

package tn.souktn.[service].service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class [Entity]Service {
    
    private final [Entity]Repository repository;
    
    // Implement these 7 methods:
    public [Entity] create([Entity] entity) { ... }
    
    @Transactional(readOnly = true)
    public List<[Entity]> findAll() { ... }
    
    @Transactional(readOnly = true)
    public [Entity] findById(Long id) { ... }
    
    // Additional query methods
    @Transactional(readOnly = true)
    public List<[Entity]> findBy[CustomField]([Type] value) { ... }
    
    public [Entity] update(Long id, [Entity] data) { ... }
    
    public void delete(Long id) { ... }
}
```

### Step 3: Create Controllers with Swagger
```java
// Template: src/main/java/tn/souktn/[service]/controller/[Entity]Controller.java

package tn.souktn.[service].controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/[entities]")
@RequiredArgsConstructor
@Tag(name = "[Entity] Management", description = "Full CRUD operations for [Entity]")
public class [Entity]Controller {

    private final [Entity]Service service;

    @GetMapping
    @Operation(summary = "Get all [entities]", description = "Retrieve all [entity] records")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public List<[Entity]> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get [entity] by ID", description = "Retrieve specific [entity]")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "[Entity] found"),
        @ApiResponse(responseCode = "404", description = "[Entity] not found")
    })
    public [Entity] findById(@Parameter(description = "The ID") @PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create [entity]", description = "Create new [entity] record")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "[Entity] created"),
        @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    public ResponseEntity<[Entity]> create(@Valid @RequestBody [Entity] entity) {
        [Entity] created = service.create(entity);
        return ResponseEntity.created(URI.create("/api/[entities]/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update [entity]", description = "Update [entity] details")
    public [Entity] update(@PathVariable Long id, @Valid @RequestBody [Entity] entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete [entity]", description = "Delete [entity] record")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "404", description = "[Entity] not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Step 4: Create API Documentation
Create `[SERVICE]_API_DOCUMENTATION.md` with:
- Full endpoint reference
- Request/response examples
- curl command examples
- Quick start workflow
- Error codes and meanings

---

## 📋 Service Implementation Checklist

### For Each Service

| Service | Entities | Status | Priority |
|---------|----------|--------|----------|
| **Payment** (8084) | Payment, Invoice, InvoiceLine, Refund | ✅ DONE | - |
| **Delivery** (8085) | Shipment, Tracking, DeliveryLocation, DeliveryMan | ⏳ TODO | HIGH |
| **Order** (8083) | Order, OrderItem, Cart, OrderHistory | ⏳ TODO | HIGH |
| **Catalog** (8082) | Product, Category, Review, Stock | ⏳ TODO | HIGH |
| **User** (8081) | User, Vendor, Shop, Address | ⏳ TODO | HIGH |
| **Dispute** (8086) | Dispute, DisputeMessage, Resolution | ⏳ TODO | MEDIUM |
| **Notification** (8087) | Notification, Review, Rating | ⏳ TODO | MEDIUM |

### Per-Service Checklist

For each service, create:

- [ ] **Repositories** (1 per entity)
  - [ ] Query methods for common searches
  - [ ] Custom finders (by status, by user, etc.)

- [ ] **Services** (1 per entity or grouped)
  - [ ] `create()` - Create new record
  - [ ] `findAll()` - Get all records
  - [ ] `findById(Long id)` - Get by ID
  - [ ] `findBy[CustomField]()` - Custom queries
  - [ ] `update()` - Update record
  - [ ] `delete()` - Delete record
  - [ ] Add `@Transactional` annotations

- [ ] **Controllers** (1 per entity)
  - [ ] GET `/api/[entities]` - Get all (top)
  - [ ] GET `/api/[entities]/{id}` - Get by ID
  - [ ] GET `/api/[entities]/?filter=value` - Filtered queries
  - [ ] POST `/api/[entities]` - Create
  - [ ] PUT `/api/[entities]/{id}` - Update
  - [ ] PATCH `/api/[entities]/{id}/status` - Status changes
  - [ ] DELETE `/api/[entities]/{id}` - Delete
  - [ ] Add Swagger annotations to ALL endpoints
  - [ ] Document response codes (200, 201, 400, 404, 409, 500)

- [ ] **Swagger Annotations**
  - [ ] `@Tag` on controller class
  - [ ] `@Operation` on every endpoint
  - [ ] `@ApiResponse` for each HTTP status code
  - [ ] `@Parameter` for all path/query parameters
  - [ ] `@RequestBody` with descriptions
  - [ ] Example values in schema

- [ ] **Documentation** (`[SERVICE]_API_DOCUMENTATION.md`)
  - [ ] Table of contents with quick links
  - [ ] Endpoint reference (all endpoints listed)
  - [ ] Request/response examples (JSON format)
  - [ ] curl command examples for each endpoint
  - [ ] Error codes and meanings
  - [ ] Quick start examples (complete workflow)
  - [ ] Testing checklist

- [ ] **Testing**
  - [ ] All endpoints testable in Swagger UI
  - [ ] All CRUD operations work
  - [ ] Error handling (404, 400, 409 responses)
  - [ ] Validation works
  - [ ] Transactions properly managed

- [ ] **Build & Deploy**
  - [ ] `mvn clean package` succeeds
  - [ ] Service starts with `mvn spring-boot:run`
  - [ ] Swagger UI accessible
  - [ ] Registered in Eureka
  - [ ] All endpoints respond

---

## 🎨 Naming Conventions

### Controllers
```
✅ PaymentController        (Plural resource name)
✅ InvoiceController        (One controller per main entity)
✅ InvoiceLineController    (Separate if significant entity)
❌ PaymentManagementController (Too long)
❌ PaymentCtrl             (Unclear abbreviation)
```

### Repositories
```
✅ PaymentRepository
✅ InvoiceRepository
❌ PaymentDAO              (Use Repository, not DAO in Spring)
❌ PaymentDB               (Unclear)
```

### Services
```
✅ PaymentService
✅ InvoiceService
❌ PaymentManager          (Use Service in Spring)
❌ PaymentHandler          (Unclear responsibility)
```

### Endpoints
```
✅ /api/payments           (Plural nouns)
✅ /api/payments/{id}      (Resource identifier)
✅ /api/payments/order/{id} (Hierarchical relationships)
✅ /api/payments?status=PENDING (Query parameters)
❌ /api/getPayments        (Verb in name - REST uses HTTP method)
❌ /api/payment            (Singular - should be plural)
❌ /api/payment/all        (Redundant - GET implies all)
```

### Methods
```
✅ findAll()
✅ findById(Long id)
✅ findByStatus(PaymentStatus status)
✅ create(Payment payment)
✅ update(Long id, Payment payment)
✅ delete(Long id)
❌ getPayment()           (Use find[])
❌ createNewPayment()     (Redundant - create implies new)
❌ deletePaymentById()    (Redundant - delete implies by id)
```

---

## 🔑 Key Best Practices

### 1. **Endpoint Ordering** (Most Important!)
```
Priority 1: GET /api/[entities]           ← Call this first
Priority 2: GET /api/[entities]/{id}      ← Specific queries
Priority 3: GET /api/[entities]?filter=x  ← Filtered queries
Priority 4: POST /api/[entities]          ← Create
Priority 5: PUT /api/[entities]/{id}      ← Update
Priority 6: PATCH /api/[entities]/{id}... ← Partial updates
Priority 7: DELETE /api/[entities]/{id}   ← Delete
```

### 2. **Swagger Documentation** (Every Endpoint)
```java
@GetMapping
@Operation(
    summary = "Brief title (5-10 words)",
    description = "Detailed description of what this does (1-2 sentences)"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Success case"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "404", description = "Not found")
})
public List<Payment> findAll(
    @Parameter(description = "Optional filter description")
    @RequestParam(required = false) String filter
) {
    ...
}
```

### 3. **HTTP Status Codes**
```
200 OK           - GET success, PUT success
201 Created      - POST success (include Location header)
204 No Content   - DELETE success (no body)
400 Bad Request  - Invalid input, validation error
404 Not Found    - Resource doesn't exist
409 Conflict     - Duplicate resource (e.g., duplicate payment for order)
500 Server Error - Internal error
```

### 4. **Validation**
```java
@NotNull
@NotBlank
@Positive
@Min @Max
@Pattern
@Valid  // For cascading validation on nested objects
```

### 5. **Transactions**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class [Entity]Service {
    
    @Transactional(readOnly = true)  // Read operations
    public [Entity] findById(Long id) { ... }
    
    // Write operations use class-level @Transactional
    public [Entity] create([Entity] entity) { ... }
}
```

---

## 📚 Documentation Template

For each service, create `[SERVICE]_API_DOCUMENTATION.md`:

```markdown
# [Service] API - Complete Reference
**Version:** 1.0.0
**Base URL:** http://localhost:[PORT]
**Service Name:** [service-name] (Eureka)

## Table of Contents
1. Overview
2. Authentication & Security
3. [Entity1] Endpoints
4. [Entity2] Endpoints
5. Response Formats
6. Error Handling
7. Swagger UI Access
8. Quick Start Examples

## [Entity1] Endpoints

### 1. Get All [Entities]
GET /api/[entities]
...

### 2. Get [Entity] by ID
GET /api/[entities]/{id}
...

[Continue for all endpoints]
```

---

## 🚀 Implementation Order

**Phase 1 (Done):**
- ✅ Payment Service (8084)

**Phase 2 (Next):**
- ⏳ Delivery Service (8085)
- ⏳ Order Service (8083)
- ⏳ Catalog Service (8082)

**Phase 3:**
- ⏳ User Service (8081)
- ⏳ Dispute Service (8086)
- ⏳ Notification Service (8087)

---

## 🧪 Testing Workflow

For each service:

1. **Build**: `mvn clean package` → BUILD SUCCESS
2. **Run**: `mvn spring-boot:run`
3. **Swagger**: Open http://localhost:[PORT]/swagger-ui.html
4. **Test**: Click each endpoint in Swagger UI
5. **Verify**: Try different parameter combinations
6. **Error**: Test 404, 400, 409 responses

---

## 📊 Delivery Service (Next in Phase 2)

### Entities (Typical)
```
Shipment          - Main shipment record
ShipmentTracking  - Status tracking
DeliveryMan       - Delivery person assignment
DeliveryLocation  - Delivery address
```

### Expected Endpoints (~30)
```
Shipment:
  GET /api/shipments
  GET /api/shipments/{id}
  GET /api/shipments/order/{orderId}
  GET /api/shipments/status/{status}
  POST /api/shipments
  PUT /api/shipments/{id}
  PATCH /api/shipments/{id}/status
  DELETE /api/shipments/{id}

ShipmentTracking:
  GET /api/tracking/{shipmentId}
  GET /api/tracking/status/{status}
  POST /api/tracking
  PATCH /api/tracking/{id}/status

[Similar pattern for DeliveryMan and DeliveryLocation]
```

---

## ✅ Quality Checklist

For each service implementation:

- [ ] All repositories have custom query methods
- [ ] All services have @Transactional management
- [ ] All controllers organized: GET all first
- [ ] Every endpoint has Swagger annotations
- [ ] Response codes documented (200, 201, 400, 404, 409, 500)
- [ ] Parameters have descriptions
- [ ] Validation annotations present (@NotNull, etc.)
- [ ] Error handling implemented (exceptions, GlobalExceptionHandler)
- [ ] API documentation file created with examples
- [ ] Build succeeds without errors
- [ ] Service starts successfully
- [ ] Swagger UI accessible and functional
- [ ] All 30+ endpoints testable in Swagger
- [ ] Registered in Eureka (8761)

---

## 🎓 Summary

**Total Services:** 7  
**Expected Endpoints:** 30+ per service = 210+ total  
**Expected Controllers:** 7 × 4 = 28 controllers  
**Expected Services:** 7 × 4 = 28 services  
**Expected Repositories:** 7 × 4 = 28 repositories  
**Documentation:** 1 per service = 7 markdown files  

**Timeline:**
- Phase 1 (Payment): ✅ DONE
- Phase 2 (3 services): ~1 day each = 3 days
- Phase 3 (3 services): ~1 day each = 3 days
- **Total: ~6 days for complete implementation**

---

## 📞 Questions?

Refer to:
1. **Payment Service Implementation**: Review `PAYMENT_SERVICE_COMPLETE.md`
2. **API Patterns**: Check `PAYMENT_API_DOCUMENTATION.md`
3. **Code Examples**: Look at Payment controllers in `Backend/payment-service/src/main/java`

---

**Version:** 1.0  
**Last Updated:** 2026-11-27  
**Status:** Phase 1 Complete ✅ | Phase 2 Ready ⏳
