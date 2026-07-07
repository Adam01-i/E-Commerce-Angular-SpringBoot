package com.ecommerce.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service", url = "${order.service.url:http://order-service:8083}")
public interface OrderClient {

    @PutMapping("/api/commandes/{id}/statut")
    void modifierStatutCommande(@PathVariable("id") Long commandeId, @RequestBody Map<String, String> body);
}