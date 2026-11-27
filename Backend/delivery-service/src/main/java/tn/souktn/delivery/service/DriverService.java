package tn.souktn.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.delivery.entity.Driver;
import tn.souktn.delivery.exception.ResourceNotFoundException;
import tn.souktn.delivery.repository.DriverRepository;

import java.util.List;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Driver> getAvailableDrivers(String zone) {
        if (zone != null && !zone.isBlank()) {
            return driverRepository.findByZoneIgnoreCaseAndAvailableTrue(zone);
        }
        return driverRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    public Driver updateDriver(Long id, Driver updated) {
        Driver existing = getDriverById(id);
        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setVehicleType(updated.getVehicleType());
        existing.setZone(updated.getZone());
        existing.setAvailable(updated.isAvailable());
        return driverRepository.save(existing);
    }

    public void deleteDriver(Long id) {
        Driver existing = getDriverById(id);
        driverRepository.delete(existing);
    }
}
