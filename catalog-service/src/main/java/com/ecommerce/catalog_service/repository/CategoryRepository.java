package com.ecommerce.catalog_service.repository;



import com.ecommerce.catalog_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Pour retrouver une catégorie par son nom exact
    Optional<Category> findByNom(String nom);
    
    // Pour filtrer les catégories par leur statut (ex: récupérer uniquement les catégories "ACTIF")
    List<Category> findByStatut(String statut);
}