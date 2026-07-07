package com.ecommerce.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(nullable = false)
private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
}

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifiant de la commande concernée
     */
    @Column(nullable = false)
    private Long commandeId;

    /**
     * Montant payé
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    /**
     * Exemple : ORANGE_MONEY, WAVE, CARTE_BANCAIRE...
     */
    @Column(nullable = false)
    private String modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus statut;

    @Column(nullable = false)
    private LocalDateTime datePaiement;

}