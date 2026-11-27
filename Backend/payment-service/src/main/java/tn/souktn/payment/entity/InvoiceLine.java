package tn.souktn.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private Long productId;     // référence au produit (MS1), par identifiant
    @NotNull private Long shopId;        // boutique du vendeur (MS0)
    @NotBlank private String productName; // copie : la facture ne change pas si le produit change
    @NotNull @Positive private BigDecimal unitPrice;
    @Positive private int quantity;
}