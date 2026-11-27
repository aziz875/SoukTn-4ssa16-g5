package tn.souktn.payment.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.souktn.payment.entity.*;
import tn.souktn.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedPayments(PaymentRepository repository) {
        return args -> {
            if (repository.count() > 0) return;   // ne rien faire si des données existent déjà

            // Paiement 1 : en attente, facture brouillon avec 2 lignes
            Payment p1 = payment(1001L, "client-123", "150.500", PaymentMethod.CARD, PaymentStatus.PENDING);
            p1.setInvoice(invoice("INV-0001", InvoiceStatus.DRAFT,
                    line(1L, 10L, "Chéchia traditionnelle", "45.500", 2),
                    line(2L, 11L, "Huile d'olive 1L", "59.500", 1)));

            // Paiement 2 : payé, facture émise
            Payment p2 = payment(1002L, "client-456", "89.000", PaymentMethod.WALLET, PaymentStatus.COMPLETED);
            p2.setProviderRef("KONNECT-TEST-0002");
            Invoice inv2 = invoice("INV-0002", InvoiceStatus.ISSUED,
                    line(3L, 12L, "Poterie de Nabeul", "89.000", 1));
            inv2.setIssuedAt(LocalDateTime.now());
            p2.setInvoice(inv2);

            // Paiement 3 : payé puis remboursé partiellement après un litige
            Payment p3 = payment(1003L, "client-123", "120.000", PaymentMethod.CARD, PaymentStatus.PARTIALLY_REFUNDED);
            Invoice inv3 = invoice("INV-0003", InvoiceStatus.ISSUED,
                    line(4L, 10L, "Tapis berbère", "120.000", 1));
            inv3.setIssuedAt(LocalDateTime.now());
            p3.setInvoice(inv3);
            Refund refund = new Refund();
            refund.setDisputeId(501L);
            refund.setAmount(new BigDecimal("40.000"));
            refund.setReason("Produit abîmé à la livraison");
            refund.setStatus(RefundStatus.COMPLETED);
            p3.getRefunds().add(refund);

            repository.saveAll(List.of(p1, p2, p3));
        };
    }

    private static Payment payment(Long orderId, String userId, String amount,
                                   PaymentMethod method, PaymentStatus status) {
        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setUserId(userId);
        p.setAmount(new BigDecimal(amount));
        p.setMethod(method);
        p.setStatus(status);
        return p;
    }

    private static Invoice invoice(String number, InvoiceStatus status, InvoiceLine... lines) {
        Invoice i = new Invoice();
        i.setInvoiceNumber(number);
        i.setStatus(status);
        i.getLines().addAll(List.of(lines));
        return i;
    }

    private static InvoiceLine line(Long productId, Long shopId, String name, String price, int qty) {
        InvoiceLine l = new InvoiceLine();
        l.setProductId(productId);
        l.setShopId(shopId);
        l.setProductName(name);
        l.setUnitPrice(new BigDecimal(price));
        l.setQuantity(qty);
        return l;
    }
}