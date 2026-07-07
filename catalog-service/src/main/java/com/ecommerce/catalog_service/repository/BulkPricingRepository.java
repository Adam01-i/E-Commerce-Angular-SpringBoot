package com.ecommerce.catalog_service.repository;


import com.ecommerce.catalog_service.model.BulkPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BulkPricingRepository extends JpaRepository<BulkPricing, Long> {
    
    // Récupérer tous les paliers de prix d'un produit spécifique
    List<BulkPricing> findByProductId(Long productId);
    
    // Requête cruciale : Trouver les paliers ACTIFS applicables pour un produit donné, 
    // où la quantité minimale est inférieure ou égale à la quantité demandée par le client,
    // triés du plus grand palier au plus petit.
    @Query("SELECT bp FROM BulkPricing bp WHERE bp.product.id = :productId " +
           "AND bp.statut = 'ACTIF' " +
           "AND bp.quantiteMinimale <= :quantite " +
           "ORDER BY bp.quantiteMinimale DESC")
    List<BulkPricing> findApplicablePricings(@Param("productId") Long productId, @Param("quantite") Integer quantite);
}