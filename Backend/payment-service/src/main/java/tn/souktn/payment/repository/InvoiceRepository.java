package tn.souktn.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.payment.entity.Invoice;
import tn.souktn.payment.entity.InvoiceStatus;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByStatus(InvoiceStatus status);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
