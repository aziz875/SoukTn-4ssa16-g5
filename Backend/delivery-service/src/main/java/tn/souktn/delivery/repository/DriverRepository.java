package tn.souktn.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.delivery.entity.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByAvailableTrue();
    List<Driver> findByZoneIgnoreCaseAndAvailableTrue(String zone);
    long countByAvailableTrue();
}
