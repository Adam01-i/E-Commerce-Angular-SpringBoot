package com.ecommerce.catalog_service.mapper;

import com.ecommerce.catalog_service.dto.request.BulkPricingRequestDTO;
import com.ecommerce.catalog_service.dto.response.BulkPricingDTO;
import com.ecommerce.catalog_service.model.BulkPricing;
import org.springframework.stereotype.Component;

@Component
public class BulkPricingMapper {

    public BulkPricingDTO toDTO(BulkPricing bulkPricing) {
        if (bulkPricing == null) {
            return null;
        }
        return BulkPricingDTO.builder()
                .id(bulkPricing.getId())
                .quantiteMinimale(bulkPricing.getQuantiteMinimale())
                .prix(bulkPricing.getPrix())
                .statut(bulkPricing.getStatut())
                .build();
    }

    public BulkPricing toEntity(BulkPricingRequestDTO request) {
        BulkPricing bulkPricing = new BulkPricing();
        bulkPricing.setQuantiteMinimale(request.getQuantiteMinimale());
        bulkPricing.setPrix(request.getPrix());
        bulkPricing.setStatut("ACTIF");
        return bulkPricing;
    }
}
