package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByCommandeId(Long commandeId);
}
