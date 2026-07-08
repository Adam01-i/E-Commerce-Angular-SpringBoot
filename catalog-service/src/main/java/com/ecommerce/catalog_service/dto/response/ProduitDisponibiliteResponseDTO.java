package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProduitDisponibiliteResponseDTO {

    private Long produitId;

    private Integer quantiteDemandee;

    private boolean disponible;
}