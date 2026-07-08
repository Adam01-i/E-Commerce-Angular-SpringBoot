package com.ecommerce.catalog_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entité JPA représentant un palier de prix de gros pour un produit.
 * Comme pour Product et Category, aucune annotation Jackson : la
 * sérialisation passe exclusivement par BulkPricingDTO / BulkPricingMapper.
 */
@Entity
@Table(name = "prix_gros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Product product;

    @NotNull(message = "La quantité minimale est obligatoire")
    @Min(value = 1, message = "La quantité minimale doit être au moins de 1")
    @Column(name = "quantite_minimale", nullable = false)
    private Integer quantiteMinimale;

    @NotNull(message = "Le prix de gros est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix de gros ne peut pas être négatif")
    @Column(name = "prix", nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF";
}
