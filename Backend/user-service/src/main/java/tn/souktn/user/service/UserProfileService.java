package tn.souktn.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.souktn.user.dto.CreateUserProfileRequest;
import tn.souktn.user.dto.UpdateUserProfileRequest;
import tn.souktn.user.entity.UserProfile;
import tn.souktn.user.exception.ConflictException;
import tn.souktn.user.exception.ResourceNotFoundException;
import tn.souktn.user.repository.UserProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository repository;

    @Transactional
    public UserProfile create(CreateUserProfileRequest request) {
        if (repository.existsById(request.id())) {
            throw new ConflictException("A profile with this Keycloak ID already exists");
        }
        if (repository.existsByEmail(request.email())) {
            throw new ConflictException("A profile with this email already exists");
        }
        UserProfile profile = new UserProfile();
        profile.setId(request.id());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setEmail(request.email());
        profile.setPhone(request.phone());
        return repository.save(profile);
    }

    public List<UserProfile> findAll() {
        return repository.findAll();
    }

    public UserProfile findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found: " + id));
    }

    @Transactional
    public UserProfile update(String id, UpdateUserProfileRequest update) {
        UserProfile profile = findById(id);
        repository.findByEmail(update.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("A profile with this email already exists");
                });
        profile.setFirstName(update.firstName());
        profile.setLastName(update.lastName());
        profile.setEmail(update.email());
        profile.setPhone(update.phone());
        return repository.save(profile);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User profile not found: " + id);
        }
        repository.deleteById(id);
    }
}
