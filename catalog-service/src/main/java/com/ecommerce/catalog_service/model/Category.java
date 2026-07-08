package com.ecommerce.catalog_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant une catégorie de produits.
 *
 * IMPORTANT : cette classe ne porte plus aucune annotation Jackson
 * (@JsonIgnore, @JsonManagedReference, @JsonBackReference). La sérialisation
 * JSON n'est plus la responsabilité de l'entité : c'est le rôle des DTO
 * (CategoryResponseDTO, CategorySimpleDTO) et du CategoryMapper. L'entité
 * n'existe que pour la persistance et les relations JPA.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF";

    /**
     * Côté inverse de la relation, jamais sérialisé (pas de @JsonIgnore
     * nécessaire ici puisque cette entité n'est plus jamais renvoyée
     * directement par un contrôleur). mappedBy = "category" correspond au
     * nom du champ dans Product.
     */
    @OneToMany(mappedBy = "category")
    private List<Product> produits = new ArrayList<>();
}
