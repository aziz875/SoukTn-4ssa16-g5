package tn.souktn.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.souktn.user.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    boolean existsByEmail(String email);
    Optional<UserProfile> findByEmail(String email);
}
