package tn.souktn.notification.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.notification.user.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    boolean existsByEmail(String email);
    Optional<UserProfile> findByEmail(String email);
}
