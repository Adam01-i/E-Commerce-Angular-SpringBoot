package com.ecommerce.payment.service.impl;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.exception.PaymentAlreadyValidatedException;
import com.ecommerce.payment.exception.PaymentNotFoundException;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        logger.info("Création d'un paiement pour la commande {}", request.commandeId());

        if (paymentRepository.existsByCommandeIdAndStatut(
                request.commandeId(),
                PaymentStatus.VALIDE)) {

            throw new PaymentAlreadyValidatedException(
                    "Cette commande est déjà payée.");
        }

        Payment payment = Payment.builder()
                .commandeId(request.commandeId())
                .montant(request.montant())
                .modePaiement(request.modePaiement())
                .statut(PaymentStatus.EN_ATTENTE)
                .datePaiement(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        logger.info("Paiement créé avec succès. ID = {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {

        logger.info("Recherche du paiement {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Paiement introuvable."));

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByCommande(Long commandeId) {

        logger.info("Recherche des paiements de la commande {}", commandeId);

        return paymentRepository.findAllByCommandeId(commandeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PaymentResponse validatePayment(Long id) {

        logger.info("Validation du paiement {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Paiement introuvable."));

        payment.setStatut(PaymentStatus.VALIDE);

        Payment saved = paymentRepository.save(payment);

        logger.info("Paiement {} validé", id);

        return mapToResponse(saved);
    }

    @Override
    public PaymentResponse rejectPayment(Long id) {

        logger.info("Rejet du paiement {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Paiement introuvable."));

        payment.setStatut(PaymentStatus.ECHOUE);

        Payment saved = paymentRepository.save(payment);

        logger.warn("Paiement {} rejeté", id);

        return mapToResponse(saved);
    }

    private PaymentResponse mapToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getCommandeId(),
                payment.getMontant(),
                payment.getModePaiement(),
                payment.getStatut(),
                payment.getDatePaiement()
        );
    }
}