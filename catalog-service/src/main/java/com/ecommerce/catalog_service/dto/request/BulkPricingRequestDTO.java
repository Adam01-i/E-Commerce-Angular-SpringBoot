package com.ecommerce.catalog_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO reçu par POST /api/produits/{id}/prix-gros et PUT /api/prix-gros/{id}.
 * Remplace l'exposition directe de l'entité BulkPricing en entrée.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPricingRequestDTO {

    @NotNull(message = "La quantité minimale est obligatoire")
    @Min(value = 1, message = "La quantité minimale doit être au moins de 1")
    private Integer quantiteMinimale;

    @NotNull(message = "Le prix de gros est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix de gros ne peut pas être négatif")
    private BigDecimal prix;
}
