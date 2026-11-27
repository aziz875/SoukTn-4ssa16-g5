package tn.souktn.dispute.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.dispute.entity.Dispute;
import tn.souktn.dispute.exception.DisputeNotFoundException;
import tn.souktn.dispute.repository.DisputeRepository;

import java.util.List;

@Service
@Transactional
public class DisputeService {

    private final DisputeRepository repository;

    public DisputeService(DisputeRepository repository) {
        this.repository = repository;
    }

    public Dispute create(Dispute dispute) {
        dispute.setId(null); // l'id est toujours généré par la base
        return repository.save(dispute);
    }

    @Transactional(readOnly = true)
    public List<Dispute> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Dispute findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DisputeNotFoundException(id));
    }

    public Dispute update(Long id, Dispute data) {
        Dispute existing = findById(id);
        existing.setOrderId(data.getOrderId());
        existing.setCustomerId(data.getCustomerId());
        existing.setVendorId(data.getVendorId());
        existing.setReason(data.getReason());
        existing.setDescription(data.getDescription());
        if (data.getStatus() != null) {
            existing.setStatus(data.getStatus());
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        Dispute existing = findById(id);
        repository.delete(existing);
    }
}
