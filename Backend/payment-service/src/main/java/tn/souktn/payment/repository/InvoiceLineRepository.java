package tn.souktn.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.payment.entity.InvoiceLine;

import java.util.List;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    List<InvoiceLine> findByProductId(Long productId);

    List<InvoiceLine> findByShopId(Long shopId);
}
