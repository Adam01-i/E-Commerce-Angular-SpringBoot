package com.ecommerce.order_service.feign;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitDisponibiliteDTO {

    private Long produitId;

    private boolean disponible;

    private Integer stock;

    private BigDecimal prixApplicable;
}