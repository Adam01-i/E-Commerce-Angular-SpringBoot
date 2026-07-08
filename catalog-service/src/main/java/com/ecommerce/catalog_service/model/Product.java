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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un produit du catalogue.
 *
 * Cause principale des erreurs 500 corrigée ici : l'entité ne porte plus
 * aucune annotation Jackson (@JsonIgnoreProperties, @JsonManagedReference).
 * Le couple @JsonManagedReference/@JsonBackReference posait deux problèmes :
 * il couplait la persistance à la sérialisation, et il échouait dès que
 * Hibernate renvoyait un proxy LAZY non initialisé (LazyInitializationException
 * lors de la sérialisation en dehors de la session, une fois le contrôleur
 * atteint). Désormais, Product n'est JAMAIS sérialisé directement : seul
 * ProductResponseDTO l'est, construit explicitement par ProductMapper à
 * l'intérieur de la transaction du service.
 */
@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix ne peut pas être négatif")
    @Column(name = "prix", nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF";

    @Size(max = 255)
    @Column(name = "image_principal", length = 255)
    private String imagePrincipale;

    @Column(name = "images_secondaires", columnDefinition = "TEXT")
    private String imagesSecondaires;

    /**
     * Relation ManyToOne strictement LAZY : la catégorie n'est chargée que
     * si elle est explicitement demandée (voir ProductRepository.findWithDetailsById
     * qui utilise @EntityGraph pour l'initialiser proprement en une seule requête
     * quand le détail complet est nécessaire).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BulkPricing> prixGros = new ArrayList<>();

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

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
