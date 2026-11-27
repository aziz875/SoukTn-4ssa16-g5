package tn.souktn.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.notification.entity.Review;
import tn.souktn.notification.exception.ConflictException;
import tn.souktn.notification.exception.ResourceNotFoundException;
import tn.souktn.notification.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;

    @Transactional
    public Review create(Review review) {
        if (repository.findByProductIdAndUserId(review.getProductId(), review.getUserId()).isPresent()) {
            throw new ConflictException("The user has already reviewed this product");
        }
        return repository.save(review);
    }

    public List<Review> findByProduct(String productId) {
        return repository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    @Transactional
    public Review update(Long id, Review update) {
        Review review = findById(id);
        review.setRating(update.getRating());
        review.setComment(update.getComment());
        return repository.save(review);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found: " + id);
        }
        repository.deleteById(id);
    }

    public Double getAverageRating(String productId) {
        Double average = repository.findAverageRatingByProductId(productId);
        return average == null ? 0.0 : average;
    }
}
