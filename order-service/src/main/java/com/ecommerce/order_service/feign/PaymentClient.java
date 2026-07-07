package com.ecommerce.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment.service.url:http://payment-service:8084}")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaiementResponseDTO initierPaiement(@RequestBody InitierPaiementRequest request);
}
