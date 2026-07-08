package com.ecommerce.catalog_service.repository;

import com.ecommerce.catalog_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Product.
 *
 * findWithDetailsById utilise @EntityGraph pour charger category et prixGros
 * en une seule requête (évite le N+1 et toute LazyInitializationException
 * lorsque le mapper accède ensuite à product.getCategory().getNom() ou à
 * product.getPrixGros()). Les méthodes de liste (findByStatut, etc.) restent
 * volontairement sans EntityGraph : la liste des produits n'a besoin que de
 * la catégorie (déjà couverte par un EntityGraph dédié), pas des paliers de
 * prix de gros, pour ne pas alourdir inutilement les requêtes de catalogue.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "prixGros"})
    Optional<Product> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByStatut(String statut, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findByNomContainingIgnoreCase(String nom);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findByCategoryId(Long categoryId);
}
