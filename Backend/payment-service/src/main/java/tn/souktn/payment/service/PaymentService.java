package tn.souktn.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.payment.entity.Payment;
import tn.souktn.payment.entity.PaymentStatus;
import tn.souktn.payment.exception.ConflictException;
import tn.souktn.payment.exception.NotFoundException;
import tn.souktn.payment.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository repository;

    public Payment create(Payment payment) {
        if (repository.existsByOrderId(payment.getOrderId())) {
            throw new ConflictException("Un paiement existe déjà pour la commande " + payment.getOrderId());
        }
        payment.setId(null);
        if (payment.getInvoice() != null && payment.getInvoice().getInvoiceNumber() == null) {
            payment.getInvoice().setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return repository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll(String userId) {
        return userId == null ? repository.findAll() : repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paiement " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public Payment findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Aucun paiement pour la commande " + orderId));
    }

    public Payment update(Long id, Payment data) {
        Payment payment = findById(id);
        payment.setAmount(data.getAmount());
        payment.setCurrency(data.getCurrency());
        payment.setMethod(data.getMethod());
        return payment;   // modifié dans une transaction : Hibernate enregistre tout seul
    }

    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment payment = findById(id);
        payment.setStatus(status);
        return payment;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}