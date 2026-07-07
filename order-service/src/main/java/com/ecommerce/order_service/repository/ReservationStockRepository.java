package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.ReservationStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationStockRepository extends JpaRepository<ReservationStock, Long> {
    List<ReservationStock> findByCommandeId(Long commandeId);
    List<ReservationStock> findByExpireeFalseAndDateReservationBefore(LocalDateTime seuil);
}
