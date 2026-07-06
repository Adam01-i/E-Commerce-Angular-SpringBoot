package com.ecommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierDTO {
    private Long id;
    private Long utilisateurId;
    private LocalDateTime dateCreation;
    private List<PanierItemDTO> items;
}
