package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.PanierItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanierItemRepository extends JpaRepository<PanierItem, Long> {
}
