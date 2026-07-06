package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.CommandeDTO;
import com.ecommerce.order_service.dto.CreerCommandeRequest;
import com.ecommerce.order_service.dto.LigneCommandeDTO;
import com.ecommerce.order_service.entity.*;
import com.ecommerce.order_service.exception.RessourceNonTrouveeException;
import com.ecommerce.order_service.repository.CommandeRepository;
import com.ecommerce.order_service.repository.PanierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final PanierRepository panierRepository;

    @Transactional
    public CommandeDTO creerCommande(CreerCommandeRequest request) {
        Panier panier = panierRepository.findByUtilisateurId(request.getUtilisateurId())
                .orElseThrow(() -> new RessourceNonTrouveeException("Aucun panier trouvé pour cet utilisateur"));

        if (panier.getItems() == null || panier.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide, impossible de créer une commande");
        }

        Commande commande = new Commande();
        commande.setNumeroCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        commande.setUtilisateurId(request.getUtilisateurId());
        commande.setDateCommande(LocalDateTime.now());
        commande.setEtat(EtatCommande.EN_ATTENTE);
        commande.setAdresseLivraison(request.getAdresseLivraison());
        commande.setModePaiement(request.getModePaiement());

        List<LigneCommande> lignes = panier.getItems().stream().map(item -> {
            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setProduitId(item.getProduitId());
            ligne.setQuantite(item.getQuantite());
            ligne.setPrixUnitaire(item.getPrixUnitaire());
            return ligne;
        }).collect(Collectors.toList());

        BigDecimal montantTotal = lignes.stream()
                .map(l -> l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        commande.setLignesCommande(lignes);
        commande.setMontantTotal(montantTotal);

        Commande sauvegardee = commandeRepository.save(commande);

        // Vider le panier après validation de la commande
        panier.getItems().clear();
        panierRepository.save(panier);

        return toDTO(sauvegardee);
    }

    public CommandeDTO consulterCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande non trouvée : " + id));
        return toDTO(commande);
    }

    public List<CommandeDTO> listerCommandesUtilisateur(Long utilisateurId) {
        return commandeRepository.findByUtilisateurId(utilisateurId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CommandeDTO toDTO(Commande commande) {
        List<LigneCommandeDTO> lignes = commande.getLignesCommande() == null ? List.of() :
                commande.getLignesCommande().stream().map(l -> new LigneCommandeDTO(
                        l.getId(), l.getProduitId(), l.getQuantite(), l.getPrixUnitaire()
                )).collect(Collectors.toList());

        return new CommandeDTO(
                commande.getId(), commande.getNumeroCommande(), commande.getUtilisateurId(),
                commande.getDateCommande(), commande.getEtat(), commande.getAdresseLivraison(),
                commande.getModePaiement(), commande.getMontantTotal(), lignes
        );
    }
}
