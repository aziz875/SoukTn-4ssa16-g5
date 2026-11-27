package tn.souktn.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.souktn.notification.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(String productId);
    Optional<Review> findByProductIdAndUserId(String productId, String userId);

    @Query("select avg(r.rating) from Review r where r.productId = :productId")
    Double findAverageRatingByProductId(String productId);
}
