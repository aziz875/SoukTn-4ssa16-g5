package tn.souktn.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.payment.entity.Refund;
import tn.souktn.payment.entity.RefundStatus;
import tn.souktn.payment.exception.NotFoundException;
import tn.souktn.payment.repository.RefundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundService {

    private final RefundRepository repository;

    public Refund create(Refund refund) {
        refund.setId(null);
        refund.setStatus(RefundStatus.PENDING);
        return repository.save(refund);
    }

    @Transactional(readOnly = true)
    public List<Refund> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Refund findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Remboursement " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Refund> findByDisputeId(Long disputeId) {
        return repository.findByDisputeId(disputeId);
    }

    @Transactional(readOnly = true)
    public List<Refund> findByStatus(RefundStatus status) {
        return repository.findByStatus(status);
    }

    public Refund update(Long id, Refund data) {
        Refund refund = findById(id);
        if (data.getReason() != null) {
            refund.setReason(data.getReason());
        }
        if (data.getAmount() != null) {
            refund.setAmount(data.getAmount());
        }
        return refund;
    }

    public Refund updateStatus(Long id, RefundStatus status) {
        Refund refund = findById(id);
        refund.setStatus(status);
        return refund;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
