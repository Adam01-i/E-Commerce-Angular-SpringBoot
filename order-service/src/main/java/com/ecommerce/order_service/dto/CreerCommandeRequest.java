package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreerCommandeRequest {

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String adresseLivraison;

    @NotBlank(message = "Le mode de paiement est obligatoire")
    private String modePaiement;
}
