package tn.souktn.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.payment.entity.Refund;
import tn.souktn.payment.entity.RefundStatus;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByDisputeId(Long disputeId);

    List<Refund> findByStatus(RefundStatus status);

    Optional<Refund> findByDisputeIdAndStatus(Long disputeId, RefundStatus status);
}
