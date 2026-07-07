package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.entity.EtatCommande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {
    private Long id;
    private String numeroCommande;
    private Long utilisateurId;
    private LocalDateTime dateCommande;
    private EtatCommande etat;
    private String adresseLivraison;
    private String modePaiement;
    private BigDecimal montantTotal;
    private List<LigneCommandeDTO> lignesCommande;
}
