package tn.souktn.dispute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.dispute.entity.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
}
