package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment API", description = "Gestion des paiements")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Créer un paiement")
    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @Operation(summary = "Rechercher un paiement par son identifiant")
    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "Lister les paiements d'une commande")
    @GetMapping("/commande/{commandeId}")
    public List<PaymentResponse> getPaymentsByCommande(@PathVariable Long commandeId) {
        return paymentService.getPaymentsByCommande(commandeId);
    }

    @Operation(summary = "Valider un paiement")
    @PutMapping("/{id}/validate")
    public PaymentResponse validatePayment(@PathVariable Long id) {
        return paymentService.validatePayment(id);
    }

    @Operation(summary = "Rejeter un paiement")
    @PutMapping("/{id}/reject")
    public PaymentResponse rejectPayment(@PathVariable Long id) {
        return paymentService.rejectPayment(id);
    }
}