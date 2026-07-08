package com.ecommerce.catalog_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correspond au BIGSERIAL
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Le prix ne peut pas être négatif")
    @Column(name = "prix", nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF"; // Valeur par défaut : ACTIF ou MASQUE

    @Size(max = 255)
    @Column(name = "image_principal", length = 255)
    private String imagePrincipale;

    @Column(name = "images_secondaires", columnDefinition = "TEXT")
    private String imagesSecondaires; // Liste d'URLs stockées sous forme de texte (séparées par des virgules par exemple)

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Category category;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<BulkPricing> prixGros = new ArrayList<>();

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Déclencheurs JPA pour injecter automatiquement les dates
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}