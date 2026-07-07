package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.FactureDTO;
import com.ecommerce.order_service.entity.Commande;
import com.ecommerce.order_service.entity.EtatCommande;
import com.ecommerce.order_service.entity.Facture;
import com.ecommerce.order_service.exception.RessourceNonTrouveeException;
import com.ecommerce.order_service.repository.CommandeRepository;
import com.ecommerce.order_service.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;
    private final CommandeRepository commandeRepository;

    @Transactional
    public FactureDTO genererFacture(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande non trouvée : " + commandeId));

        if (commande.getEtat() != EtatCommande.PAYEE) {
            throw new IllegalStateException("Impossible de générer une facture : la commande n'est pas payée");
        }

        Facture facture = new Facture();
        facture.setCommande(commande);
        facture.setNumeroFacture("FACT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        facture.setDateEmission(LocalDateTime.now());
        facture.setMontantTotal(commande.getMontantTotal());

        Facture sauvegardee = factureRepository.save(facture);
        return toDTO(sauvegardee);
    }

    public FactureDTO consulterFacture(Long commandeId) {
        Facture facture = factureRepository.findByCommandeId(commandeId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Facture non trouvée pour la commande : " + commandeId));
        return toDTO(facture);
    }

    private FactureDTO toDTO(Facture facture) {
        return new FactureDTO(
                facture.getId(), facture.getCommande().getId(),
                facture.getNumeroFacture(), facture.getDateEmission(), facture.getMontantTotal()
        );
    }
}
