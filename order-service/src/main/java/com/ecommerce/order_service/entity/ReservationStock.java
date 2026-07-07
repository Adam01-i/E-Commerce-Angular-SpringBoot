package com.ecommerce.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commande_id", nullable = false)
    private Long commandeId;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @Column(name = "expiree", nullable = false)
    private boolean expiree = false;
}
