package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDisponibiliteResponseDTO {

    private Long produitId;

    private Integer quantiteDemandee;

    private boolean disponible;

    private Integer stock;

    private BigDecimal prixApplicable;
}