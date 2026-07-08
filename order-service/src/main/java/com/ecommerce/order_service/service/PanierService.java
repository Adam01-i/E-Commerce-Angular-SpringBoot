package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.AjoutPanierRequest;
import com.ecommerce.order_service.dto.PanierDTO;
import com.ecommerce.order_service.dto.PanierItemDTO;
import com.ecommerce.order_service.entity.Panier;
import com.ecommerce.order_service.entity.PanierItem;
import com.ecommerce.order_service.exception.RessourceNonTrouveeException;
import com.ecommerce.order_service.feign.CatalogClient;
import com.ecommerce.order_service.feign.ProduitDisponibiliteDTO;
import com.ecommerce.order_service.repository.PanierItemRepository;
import com.ecommerce.order_service.repository.PanierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PanierService {

    private final PanierRepository panierRepository;
    private final PanierItemRepository panierItemRepository;
    private final CatalogClient catalogClient;

    public PanierDTO consulterPanier(Long utilisateurId) {
        Panier panier = getOuCreerPanier(utilisateurId);
        return toDTO(panier);
    }

    public PanierDTO ajouterAuPanier(Long utilisateurId, AjoutPanierRequest request) {
        Panier panier = getOuCreerPanier(utilisateurId);

        // Vérifie la disponibilité et récupère le prix via Catalog Service (OpenFeign)
        ProduitDisponibiliteDTO disponibilite =
                catalogClient.verifierDisponibilite(
                        request.getProduitId(),
                        request.getQuantite()
                );

        if (!disponibilite.isDisponible() || disponibilite.getStock() < request.getQuantite()) {
            throw new IllegalStateException("Stock insuffisant pour ce produit");
        }

        PanierItem item = new PanierItem();
        item.setPanier(panier);
        item.setProduitId(request.getProduitId());
        item.setQuantite(request.getQuantite());
        item.setPrixUnitaire(disponibilite.getPrixApplicable());

        panierItemRepository.save(item);
        panier.getItems().add(item);

        return toDTO(panier);
    }

    public void supprimerArticle(Long utilisateurId, Long itemId) {
        Panier panier = getOuCreerPanier(utilisateurId);
        PanierItem item = panier.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RessourceNonTrouveeException("Article non trouvé dans le panier"));
        panier.getItems().remove(item);
        panierItemRepository.delete(item);
    }

    private Panier getOuCreerPanier(Long utilisateurId) {
        return panierRepository.findByUtilisateurId(utilisateurId)
                .orElseGet(() -> {
                    Panier nouveau = new Panier();
                    nouveau.setUtilisateurId(utilisateurId);
                    nouveau.setDateCreation(LocalDateTime.now());
                    return panierRepository.save(nouveau);
                });
    }

    private PanierDTO toDTO(Panier panier) {
        List<PanierItemDTO> items = panier.getItems() == null ? List.of() :
                panier.getItems().stream().map(i -> new PanierItemDTO(
                        i.getId(), i.getProduitId(), i.getQuantite(), i.getPrixUnitaire()
                )).collect(Collectors.toList());

        return new PanierDTO(panier.getId(), panier.getUtilisateurId(), panier.getDateCreation(), items);
    }
}
