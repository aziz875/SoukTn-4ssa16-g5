package tn.souktn.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.payment.entity.InvoiceLine;
import tn.souktn.payment.exception.NotFoundException;
import tn.souktn.payment.repository.InvoiceLineRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceLineService {

    private final InvoiceLineRepository repository;

    public InvoiceLine create(InvoiceLine invoiceLine) {
        invoiceLine.setId(null);
        return repository.save(invoiceLine);
    }

    @Transactional(readOnly = true)
    public List<InvoiceLine> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public InvoiceLine findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ligne de facture " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<InvoiceLine> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<InvoiceLine> findByShopId(Long shopId) {
        return repository.findByShopId(shopId);
    }

    public InvoiceLine update(Long id, InvoiceLine data) {
        InvoiceLine invoiceLine = findById(id);
        if (data.getProductName() != null) {
            invoiceLine.setProductName(data.getProductName());
        }
        if (data.getUnitPrice() != null) {
            invoiceLine.setUnitPrice(data.getUnitPrice());
        }
        if (data.getQuantity() > 0) {
            invoiceLine.setQuantity(data.getQuantity());
        }
        return invoiceLine;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
