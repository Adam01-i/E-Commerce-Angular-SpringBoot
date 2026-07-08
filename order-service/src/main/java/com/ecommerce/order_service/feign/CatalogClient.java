package com.ecommerce.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "catalog-service",
        url = "${catalog.service.url:http://catalog-service:8082}"
)
public interface CatalogClient {

    @GetMapping("/api/produits/{id}/disponibilite")
    ProduitDisponibiliteDTO verifierDisponibilite(
            @PathVariable("id") Long produitId,
            @RequestParam("quantite") Integer quantite
    );
}