# Payment Service API - Complete Reference
**Version:** 1.0.0  
**Base URL:** `http://localhost:8084`  
**Service Name:** `payment` (Eureka registered)  
**Last Updated:** 2026-11-27

---

## 📋 Table of Contents
1. [Overview](#overview)
2. [Authentication & Security](#authentication--security)
3. [Payment Endpoints](#payment-endpoints)
4. [Invoice Endpoints](#invoice-endpoints)
5. [Invoice Line Endpoints](#invoice-line-endpoints)
6. [Refund Endpoints](#refund-endpoints)
7. [Response Formats](#response-formats)
8. [Error Handling](#error-handling)
9. [Swagger UI Access](#swagger-ui-access)
10. [Quick Start Examples](#quick-start-examples)

---

## Overview

The Payment Service manages all payment-related operations including:
- **Payments**: Order payment processing with status tracking
- **Invoices**: Automated invoice generation with line items
- **Refunds**: Partial and full refund management linked to disputes

### Core Entities

```
Payment (1) ──→ (1) Invoice → (N) InvoiceLine
   ↓
(N) Refund
```

### Best Practices in This API

✅ **Resource naming**: Plural nouns (`/api/payments`, `/api/invoices`)  
✅ **HTTP Methods**: Standard REST (GET, POST, PUT, PATCH, DELETE)  
✅ **Endpoint ordering**: GET ALL first, then specific queries, then modifications  
✅ **Status codes**: Proper HTTP status codes (200, 201, 400, 404, 409, 500)  
✅ **Filtering**: Query parameters for optional filters  
✅ **Pagination ready**: Can be added to list endpoints  
✅ **Fully Swagger documented**: Test all endpoints in Swagger UI  

---

## Authentication & Security

**Current Status**: Demo mode (no auth required)  
**Production Ready**: Add OAuth2 Bearer token validation

```bash
# Production example (when OAuth2 is enabled)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8084/api/payments
```

---

## 💳 Payment Endpoints

### 1. Get All Payments
📌 **Most Important - Call This First**

```
GET /api/payments
```

**Description**: Retrieve all payments, optionally filtered by userId

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `userId` | string | No | Filter payments by Keycloak user ID |

**Query Examples:**
```bash
# Get all payments
curl http://localhost:8084/api/payments

# Get payments for a specific user
curl http://localhost:8084/api/payments?userId=user123

# Using jq to format JSON
curl http://localhost:8084/api/payments | jq
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderId": 100,
    "userId": "user123",
    "amount": 150.50,
    "currency": "TND",
    "status": "COMPLETED",
    "method": "CARD",
    "providerRef": "KONNECT-ABC123",
    "failureReason": null,
    "createdAt": "2026-11-27T10:30:00",
    "updatedAt": "2026-11-27T10:35:00",
    "invoice": {
      "id": 5,
      "invoiceNumber": "INV-ABC12345",
      "status": "ISSUED",
      "issuedAt": "2026-11-27T10:35:00",
      "lines": [
        {
          "id": 10,
          "productId": 50,
          "shopId": 2,
          "productName": "Nike Shoes",
          "unitPrice": 100.00,
          "quantity": 1
        },
        {
          "id": 11,
          "productId": 51,
          "shopId": 3,
          "productName": "Socks",
          "unitPrice": 50.50,
          "quantity": 1
        }
      ]
    },
    "refunds": []
  }
]
```

---

### 2. Get Payment by ID

```
GET /api/payments/{id}
```

**Description**: Retrieve a specific payment by its unique ID

**Path Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `id` | Long | Yes | Payment ID (e.g., 1, 2, 3...) |

**Examples:**
```bash
curl http://localhost:8084/api/payments/1 | jq

# With error handling
curl -i http://localhost:8084/api/payments/999  # Returns 404
```

**Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 100,
  "userId": "user123",
  "amount": 150.50,
  "currency": "TND",
  "status": "COMPLETED",
  "method": "CARD",
  "providerRef": "KONNECT-ABC123",
  "createdAt": "2026-11-27T10:30:00",
  "updatedAt": "2026-11-27T10:35:00",
  "invoice": {...},
  "refunds": []
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Paiement 999 introuvable",
  "timestamp": "2026-11-27T10:40:00"
}
```

---

### 3. Get Payment by Order ID

```
GET /api/payments/order/{orderId}
```

**Description**: Find the payment for a specific order (each order has max 1 payment)

**Path Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `orderId` | Long | Yes | Order ID from Order Service |

**Examples:**
```bash
curl http://localhost:8084/api/payments/order/100 | jq
```

**Response (200 OK):** Same as Get Payment by ID

---

### 4. Create Payment

```
POST /api/payments
```

**Description**: Create a new payment. Automatically generates invoice if provided.

**Request Body:**
```json
{
  "orderId": 100,
  "userId": "user123",
  "amount": 150.50,
  "currency": "TND",
  "method": "CARD",
  "invoice": {
    "lines": [
      {
        "productId": 50,
        "shopId": 2,
        "productName": "Nike Shoes",
        "unitPrice": 100.00,
        "quantity": 1
      },
      {
        "productId": 51,
        "shopId": 3,
        "productName": "Socks",
        "unitPrice": 50.50,
        "quantity": 1
      }
    ]
  }
}
```

**Examples:**
```bash
curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 100,
    "userId": "user123",
    "amount": 150.50,
    "currency": "TND",
    "method": "CARD",
    "invoice": {
      "lines": [
        {
          "productId": 50,
          "shopId": 2,
          "productName": "Nike Shoes",
          "unitPrice": 100.00,
          "quantity": 1
        }
      ]
    }
  }' | jq
```

**Response (201 Created):**
```json
{
  "id": 2,
  "orderId": 100,
  "userId": "user123",
  "amount": 150.50,
  "currency": "TND",
  "status": "PENDING",
  "method": "CARD",
  "createdAt": "2026-11-27T10:30:00",
  "invoice": {
    "id": 6,
    "invoiceNumber": "INV-XYZ67890",
    "status": "DRAFT"
  },
  "refunds": []
}
```

**Validation Errors (400 Bad Request):**
```json
{
  "error": "orderId is required",
  "field": "orderId",
  "timestamp": "2026-11-27T10:40:00"
}
```

**Conflict Error (409 Conflict):**
```json
{
  "error": "Un paiement existe déjà pour la commande 100",
  "timestamp": "2026-11-27T10:40:00"
}
```

---

### 5. Update Payment

```
PUT /api/payments/{id}
```

**Description**: Update payment amount, currency, or method

**Path Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `id` | Long | Yes | Payment ID |

**Request Body:**
```json
{
  "amount": 160.00,
  "currency": "TND",
  "method": "TRANSFER"
}
```

**Examples:**
```bash
curl -X PUT http://localhost:8084/api/payments/1 \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 160.00,
    "currency": "TND",
    "method": "TRANSFER"
  }' | jq
```

**Response (200 OK):** Updated payment object

---

### 6. Update Payment Status

```
PATCH /api/payments/{id}/status
```

**Description**: Change payment status (PENDING → COMPLETED, FAILED, REFUNDED, etc.)

**Path Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `id` | Long | Yes | Payment ID |

**Query Parameters:**
| Name | Type | Required | Values |
|------|------|----------|--------|
| `status` | enum | Yes | PENDING, COMPLETED, FAILED, REFUNDED, PARTIAL_REFUND |

**Examples:**
```bash
# Mark payment as completed
curl -X PATCH "http://localhost:8084/api/payments/1/status?status=COMPLETED" | jq

# Mark payment as failed
curl -X PATCH "http://localhost:8084/api/payments/1/status?status=FAILED" | jq
```

**Response (200 OK):**
```json
{
  "id": 1,
  "orderId": 100,
  "status": "COMPLETED",
  "updatedAt": "2026-11-27T10:45:00"
}
```

---

### 7. Delete Payment

```
DELETE /api/payments/{id}
```

**Description**: Delete payment and cascade delete invoices and refunds

**Path Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| `id` | Long | Yes | Payment ID |

**Examples:**
```bash
curl -X DELETE http://localhost:8084/api/payments/1 -i
# Returns 204 No Content
```

**Response (204 No Content):** Empty response

---

## 📄 Invoice Endpoints

### 1. Get All Invoices

```
GET /api/invoices
```

**Description**: Retrieve all invoices in the system

**Examples:**
```bash
curl http://localhost:8084/api/invoices | jq
```

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "invoiceNumber": "INV-ABC12345",
    "status": "ISSUED",
    "issuedAt": "2026-11-27T10:35:00",
    "lines": [
      {
        "id": 10,
        "productId": 50,
        "shopId": 2,
        "productName": "Nike Shoes",
        "unitPrice": 100.00,
        "quantity": 1
      }
    ]
  }
]
```

---

### 2. Get Invoice by ID

```
GET /api/invoices/{id}
```

**Examples:**
```bash
curl http://localhost:8084/api/invoices/5 | jq
```

---

### 3. Get Invoice by Invoice Number

```
GET /api/invoices/number/{invoiceNumber}
```

**Description**: Find invoice by its unique invoice number

**Examples:**
```bash
curl http://localhost:8084/api/invoices/number/INV-ABC12345 | jq
```

---

### 4. Get Invoices by Status

```
GET /api/invoices/status/{status}
```

**Description**: Get all invoices with specific status (DRAFT, ISSUED, PAID, CANCELLED)

**Examples:**
```bash
curl http://localhost:8084/api/invoices/status/DRAFT | jq
curl http://localhost:8084/api/invoices/status/ISSUED | jq
```

---

### 5. Create Invoice

```
POST /api/invoices
```

**Request Body:**
```json
{
  "lines": [
    {
      "productId": 50,
      "shopId": 2,
      "productName": "Nike Shoes",
      "unitPrice": 100.00,
      "quantity": 1
    }
  ]
}
```

**Examples:**
```bash
curl -X POST http://localhost:8084/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "lines": [
      {
        "productId": 50,
        "shopId": 2,
        "productName": "Nike Shoes",
        "unitPrice": 100.00,
        "quantity": 1
      }
    ]
  }' | jq
```

---

### 6. Update Invoice

```
PUT /api/invoices/{id}
```

---

### 7. Update Invoice Status

```
PATCH /api/invoices/{id}/status
```

**Query Parameters:**
| Name | Type | Values |
|------|------|--------|
| `status` | enum | DRAFT, ISSUED, PAID, CANCELLED |

**Examples:**
```bash
curl -X PATCH "http://localhost:8084/api/invoices/5/status?status=ISSUED" | jq
```

---

### 8. Delete Invoice

```
DELETE /api/invoices/{id}
```

---

## 📋 Invoice Line Endpoints

### 1. Get All Invoice Lines

```
GET /api/invoice-lines
```

---

### 2. Get Invoice Line by ID

```
GET /api/invoice-lines/{id}
```

---

### 3. Get Invoice Lines by Product ID

```
GET /api/invoice-lines/product/{productId}
```

**Description**: View all invoices containing a specific product

**Examples:**
```bash
curl http://localhost:8084/api/invoice-lines/product/50 | jq
```

---

### 4. Get Invoice Lines by Shop ID

```
GET /api/invoice-lines/shop/{shopId}
```

**Description**: View all invoices from a specific vendor/shop

**Examples:**
```bash
curl http://localhost:8084/api/invoice-lines/shop/2 | jq
```

---

### 5. Create Invoice Line

```
POST /api/invoice-lines
```

**Request Body:**
```json
{
  "productId": 50,
  "shopId": 2,
  "productName": "Nike Shoes",
  "unitPrice": 100.00,
  "quantity": 2
}
```

---

### 6. Update Invoice Line

```
PUT /api/invoice-lines/{id}
```

---

### 7. Delete Invoice Line

```
DELETE /api/invoice-lines/{id}
```

---

## 💰 Refund Endpoints

### 1. Get All Refunds

```
GET /api/refunds
```

**Description**: Retrieve all refunds in the system

**Examples:**
```bash
curl http://localhost:8084/api/refunds | jq
```

---

### 2. Get Refund by ID

```
GET /api/refunds/{id}
```

---

### 3. Get Refunds by Dispute ID

```
GET /api/refunds/dispute/{disputeId}
```

**Description**: View all refund requests for a specific dispute

**Examples:**
```bash
curl http://localhost:8084/api/refunds/dispute/10 | jq
```

---

### 4. Get Refunds by Status

```
GET /api/refunds/status/{status}
```

**Query Parameters:**
| Name | Type | Values |
|------|------|--------|
| `status` | enum | PENDING, APPROVED, REJECTED, COMPLETED |

**Examples:**
```bash
curl http://localhost:8084/api/refunds/status/PENDING | jq
curl http://localhost:8084/api/refunds/status/COMPLETED | jq
```

---

### 5. Create Refund

```
POST /api/refunds
```

**Request Body:**
```json
{
  "disputeId": 10,
  "amount": 50.00,
  "reason": "Product defective - customer returned"
}
```

**Examples:**
```bash
curl -X POST http://localhost:8084/api/refunds \
  -H "Content-Type: application/json" \
  -d '{
    "disputeId": 10,
    "amount": 50.00,
    "reason": "Product defective - customer returned"
  }' | jq
```

---

### 6. Update Refund

```
PUT /api/refunds/{id}
```

---

### 7. Update Refund Status

```
PATCH /api/refunds/{id}/status
```

**Query Parameters:**
| Name | Type | Values |
|------|------|--------|
| `status` | enum | PENDING, APPROVED, REJECTED, COMPLETED |

**Examples:**
```bash
curl -X PATCH "http://localhost:8084/api/refunds/1/status?status=APPROVED" | jq
curl -X PATCH "http://localhost:8084/api/refunds/1/status?status=COMPLETED" | jq
```

---

### 8. Delete Refund

```
DELETE /api/refunds/{id}
```

---

## Response Formats

### Success Response (200 OK / 201 Created)
```json
{
  "id": 1,
  "field1": "value1",
  "field2": "value2",
  "createdAt": "2026-11-27T10:30:00",
  "updatedAt": "2026-11-27T10:30:00"
}
```

### Error Response (4xx / 5xx)
```json
{
  "error": "Human-readable error message",
  "timestamp": "2026-11-27T10:40:00",
  "path": "/api/payments/1"
}
```

---

## Error Handling

| Status Code | Meaning | Example |
|-------------|---------|---------|
| **200** | OK - Request succeeded | GET successful |
| **201** | Created - Resource created | POST successful |
| **204** | No Content - Delete successful | DELETE successful |
| **400** | Bad Request - Invalid input | Missing required field |
| **404** | Not Found - Resource doesn't exist | Payment ID doesn't exist |
| **409** | Conflict - Duplicate resource | Payment already exists for order |
| **500** | Server Error - Internal issue | Database connection failed |

---

## Swagger UI Access

**Swagger UI:** http://localhost:8084/swagger-ui.html  
**OpenAPI JSON:** http://localhost:8084/v3/api-docs

**Features in Swagger UI:**
- 📖 Full API documentation
- 🧪 Test endpoints directly
- 📝 See request/response examples
- 🔍 Filter by tag (Payment, Invoice, etc.)
- 💬 Parameter descriptions

---

## Quick Start Examples

### Complete Payment Flow

```bash
# 1. Create a payment with invoice
PAYMENT_RESPONSE=$(curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 100,
    "userId": "user123",
    "amount": 150.50,
    "currency": "TND",
    "method": "CARD",
    "invoice": {
      "lines": [
        {
          "productId": 50,
          "shopId": 2,
          "productName": "Nike Shoes",
          "unitPrice": 100.00,
          "quantity": 1
        }
      ]
    }
  }')

echo $PAYMENT_RESPONSE | jq

# Extract payment ID
PAYMENT_ID=$(echo $PAYMENT_RESPONSE | jq -r '.id')
echo "Created Payment ID: $PAYMENT_ID"

# 2. Get the payment
curl http://localhost:8084/api/payments/$PAYMENT_ID | jq

# 3. Update payment status to COMPLETED
curl -X PATCH "http://localhost:8084/api/payments/$PAYMENT_ID/status?status=COMPLETED" | jq

# 4. Get all invoices
curl http://localhost:8084/api/invoices | jq

# 5. Get invoice by number
INVOICE_NUMBER=$(curl http://localhost:8084/api/invoices | jq -r '.[0].invoiceNumber')
curl http://localhost:8084/api/invoices/number/$INVOICE_NUMBER | jq

# 6. Create a refund
REFUND_RESPONSE=$(curl -X POST http://localhost:8084/api/refunds \
  -H "Content-Type: application/json" \
  -d '{
    "disputeId": 10,
    "amount": 50.00,
    "reason": "Customer returned product"
  }')

echo $REFUND_RESPONSE | jq

# 7. Mark refund as completed
REFUND_ID=$(echo $REFUND_RESPONSE | jq -r '.id')
curl -X PATCH "http://localhost:8084/api/refunds/$REFUND_ID/status?status=COMPLETED" | jq
```

### Useful Curl Shortcuts

```bash
# Pretty print JSON
curl http://localhost:8084/api/payments | jq

# Save response to file
curl http://localhost:8084/api/payments > payments.json

# Show headers and status code
curl -i http://localhost:8084/api/payments

# Check if endpoint exists
curl -I http://localhost:8084/api/payments

# Count total payments
curl http://localhost:8084/api/payments | jq 'length'

# Filter payments by user
curl http://localhost:8084/api/payments | jq '.[] | select(.userId=="user123")'
```

---

## Testing Checklist

Before deploying to production, test:

- [ ] Create a payment
- [ ] Get all payments
- [ ] Get payment by ID
- [ ] Get payment by order ID
- [ ] Update payment details
- [ ] Update payment status
- [ ] Delete payment
- [ ] Create invoice (optional - happens with payment)
- [ ] Get all invoices
- [ ] Get invoice by number
- [ ] Create refund
- [ ] Get refunds by dispute ID
- [ ] Update refund status
- [ ] Verify error handling (404, 400, 409 responses)

---

## Related Services

| Service | Port | Purpose |
|---------|------|---------|
| Discovery Server (Eureka) | 8761 | Service registry |
| Config Server | 8888 | Centralized configuration |
| Order Service | 8083 | Order information |
| Delivery Service | 8085 | Delivery tracking |
| Dispute Service | 8086 | Dispute management |
| User Service | 8081 | User & vendor info |

---

## Development Notes

### Running Locally
```bash
cd Backend/payment-service
mvn clean spring-boot:run
```

### Environment Variables
```bash
SERVER_PORT=8084
EUREKA_URI=http://localhost:8761/eureka
SPRING_CONFIG_IMPORT=http://localhost:8888
DB_HOST=localhost
DB_PORT=3306
DB_NAME=payment_db
DB_USER=root
DB_PASSWORD=
```

### Database
- **Type**: MySQL
- **Connection String**: `jdbc:mysql://localhost:3306/payment_db`
- **Auto-create**: Enabled
- **Schema update**: Automatic (Hibernate ddl-auto=update)

---

**Last Updated:** 2026-11-27  
**Maintained By:** Nouaigui Marouen
