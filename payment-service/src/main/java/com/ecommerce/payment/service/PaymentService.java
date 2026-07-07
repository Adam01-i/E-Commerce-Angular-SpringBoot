package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    List<PaymentResponse> getAllPayments();

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getPaymentsByCommande(Long commandeId);

    PaymentResponse validatePayment(Long id);

    PaymentResponse rejectPayment(Long id);
}