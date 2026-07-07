package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
}
