package com.ecommerce.catalog_service.repository;


import com.ecommerce.catalog_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Trouver tous les produits d'un statut donné, paginé (catalogue public : statut = ACTIF)
    Page<Product> findByStatut(String statut, Pageable pageable);

    // Trouver tous les produits d'une catégorie spécifique
    List<Product> findByCategoryId(Long categoryId);
    
    // Trouver les produits actifs d'une catégorie spécifique (pour l'affichage client)
    List<Product> findByCategoryIdAndStatut(Long categoryId, String statut);
    
    // Rechercher un produit par son nom (recherche insensible à la casse : ex: "chaussure" ou "Chaussure")
    List<Product> findByNomContainingIgnoreCase(String nom);
    
    // Trouver les produits qui sont actifs et qui ont du stock disponible
    List<Product> findByStatutAndStockGreaterThan(String statut, Integer minStock);
}