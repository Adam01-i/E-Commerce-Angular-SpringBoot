package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.CommandeDTO;
import com.ecommerce.order_service.dto.CreerCommandeRequest;
import com.ecommerce.order_service.dto.LigneCommandeDTO;
import com.ecommerce.order_service.entity.*;
import com.ecommerce.order_service.exception.RessourceNonTrouveeException;
import com.ecommerce.order_service.feign.CatalogClient;
import com.ecommerce.order_service.feign.InitierPaiementRequest;
import com.ecommerce.order_service.feign.PaiementResponseDTO;
import com.ecommerce.order_service.feign.PaymentClient;
import com.ecommerce.order_service.repository.CommandeRepository;
import com.ecommerce.order_service.repository.PanierRepository;
import com.ecommerce.order_service.repository.ReservationStockRepository;
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
    private final ReservationStockRepository reservationStockRepository;
    private final CatalogClient catalogClient;
    private final PaymentClient paymentClient;

    @Transactional
    public CommandeDTO creerCommande(CreerCommandeRequest request) {
        Panier panier = panierRepository.findByUtilisateurId(request.getUtilisateurId())
                .orElseThrow(() -> new RessourceNonTrouveeException("Aucun panier trouvé pour cet utilisateur"));

        if (panier.getItems() == null || panier.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide, impossible de créer une commande");
        }

        // 1. Créer la commande en EN_ATTENTE
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

        // 2. Réserver le stock pour chaque ligne (appel Catalog + trace locale)
        for (LigneCommande ligne : lignes) {
            catalogClient.verifierDisponibilite(ligne.getProduitId()); // vérif simple ici

            ReservationStock reservation = new ReservationStock();
            reservation.setCommandeId(sauvegardee.getId());
            reservation.setProduitId(ligne.getProduitId());
            reservation.setQuantite(ligne.getQuantite());
            reservation.setDateReservation(LocalDateTime.now());
            reservation.setExpiree(false);
            reservationStockRepository.save(reservation);
        }

        // 3. Vider le panier
        panier.getItems().clear();
        panierRepository.save(panier);

        // 4. Initier le paiement auprès du Payment Service
        InitierPaiementRequest paiementRequest = new InitierPaiementRequest(
                sauvegardee.getId(), montantTotal, request.getModePaiement()
        );
        PaiementResponseDTO paiementResponse = paymentClient.initierPaiement(paiementRequest);

        // 5. Mettre à jour le statut selon la réponse immédiate (simplifié ; en réalité
        //    le vrai passage à PAYEE se fera via un callback du Payment Service)
        if ("VALIDE".equals(paiementResponse.getStatut())) {
            sauvegardee.setEtat(EtatCommande.PAYEE);
            commandeRepository.save(sauvegardee);
        }

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

    public List<CommandeDTO> listerToutesLesCommandes() {
        return commandeRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CommandeDTO modifierStatut(Long id, EtatCommande nouvelEtat) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande non trouvée : " + id));

        validerTransition(commande.getEtat(), nouvelEtat);
        commande.setEtat(nouvelEtat);
        return toDTO(commandeRepository.save(commande));
    }

    // Machine à états simple : définit les transitions autorisées
    private void validerTransition(EtatCommande actuel, EtatCommande nouveau) {
        boolean autorise = switch (actuel) {
            case EN_ATTENTE -> nouveau == EtatCommande.PAYEE || nouveau == EtatCommande.ANNULEE;
            case PAYEE -> nouveau == EtatCommande.EXPEDIEE || nouveau == EtatCommande.ANNULEE;
            case EXPEDIEE -> nouveau == EtatCommande.LIVREE;
            case LIVREE, ANNULEE -> false; // états finaux
        };

        if (!autorise) {
            throw new IllegalStateException(
                    "Transition non autorisée : " + actuel + " -> " + nouveau);
        }
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
