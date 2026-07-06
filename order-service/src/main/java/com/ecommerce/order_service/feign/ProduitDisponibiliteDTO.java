package com.ecommerce.order_service.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDisponibiliteDTO {
    private Long produitId;
    private boolean disponible;
    private Integer stock;
    private BigDecimal prixApplicable;
}
