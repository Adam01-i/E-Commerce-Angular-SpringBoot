package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCommandeId(Long commandeId);

    List<Payment> findAllByCommandeId(Long commandeId);

    boolean existsByCommandeIdAndStatut(Long commandeId, PaymentStatus statut);

}