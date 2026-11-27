package tn.souktn.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    @NotBlank
    @Size(max = 100)
    @Column(name = "keycloak_id", nullable = false, updatable = false, length = 100)
    private String id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Size(max = 30)
    @Column(length = 30)
    private String phone;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
