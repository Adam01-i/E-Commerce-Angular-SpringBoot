package com.ecommerce.catalog_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "prix_gros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correspond au BIGSERIAL
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false) // Correspond à la clé étrangère BIGINT / FK toward PRODUITS.id
    @JsonIgnore // Évite les boucles infinies lors de la conversion en JSON (Product -> BulkPricing -> Product)
    private Product product;

    @NotNull(message = "La quantité minimale est obligatoire")
    @Min(value = 1, message = "La quantité minimale doit être au moins de 1")
    @Column(name = "quantite_minimale", nullable = false)
    private Integer quantiteMinimale;

    @NotNull(message = "Le prix de gros est obligatoire")
    @Min(value = 0, message = "Le prix de gros ne peut pas être négatif")
    @Column(name = "prix", nullable = false, precision = 12, scale = 2) // Correspond à NUMERIC(12,2)
    private Double prix;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF"; // Valeur par défaut : ACTIF ou INACTIF
}