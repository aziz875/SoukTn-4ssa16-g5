package tn.souktn.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long disputeId;              // litige à l'origine du remboursement (MS5)
    @NotNull @Positive private BigDecimal amount;
    private String reason;

    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;
}