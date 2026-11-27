package tn.souktn.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.payment.entity.Invoice;
import tn.souktn.payment.entity.InvoiceStatus;
import tn.souktn.payment.exception.ConflictException;
import tn.souktn.payment.exception.NotFoundException;
import tn.souktn.payment.repository.InvoiceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository repository;

    public Invoice create(Invoice invoice) {
        if (invoice.getInvoiceNumber() != null && repository.existsByInvoiceNumber(invoice.getInvoiceNumber())) {
            throw new ConflictException("Un numéro de facture existe déjà: " + invoice.getInvoiceNumber());
        }
        invoice.setId(null);
        invoice.setStatus(InvoiceStatus.DRAFT);
        return repository.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Facture " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public Invoice findByInvoiceNumber(String invoiceNumber) {
        return repository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new NotFoundException("Facture " + invoiceNumber + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return repository.findByStatus(status);
    }

    public Invoice update(Long id, Invoice data) {
        Invoice invoice = findById(id);
        if (data.getLines() != null) {
            invoice.setLines(data.getLines());
        }
        return invoice;
    }

    public Invoice updateStatus(Long id, InvoiceStatus status) {
        Invoice invoice = findById(id);
        invoice.setStatus(status);
        if (status == InvoiceStatus.ISSUED) {
            invoice.setIssuedAt(LocalDateTime.now());
        }
        return invoice;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
