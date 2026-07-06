package com.ecommerce.order_service.service;

import com.ecommerce.order_service.entity.Commande;
import com.ecommerce.order_service.entity.EtatCommande;
import com.ecommerce.order_service.entity.ReservationStock;
import com.ecommerce.order_service.repository.CommandeRepository;
import com.ecommerce.order_service.repository.ReservationStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationExpirationService {

    private final ReservationStockRepository reservationStockRepository;
    private final CommandeRepository commandeRepository;

    private static final int DELAI_EXPIRATION_MINUTES = 15;

    // Tourne toutes les 5 minutes (300000 ms)
    @Scheduled(fixedRate = 300000)
    public void expirerReservationsAnciennes() {
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(DELAI_EXPIRATION_MINUTES);
        List<ReservationStock> anciennes = reservationStockRepository
                .findByExpireeFalseAndDateReservationBefore(seuil);

        for (ReservationStock reservation : anciennes) {
            reservation.setExpiree(true);
            reservationStockRepository.save(reservation);

            commandeRepository.findById(reservation.getCommandeId()).ifPresent(commande -> {
                if (commande.getEtat() == EtatCommande.EN_ATTENTE) {
                    commande.setEtat(EtatCommande.ANNULEE);
                    commandeRepository.save(commande);
                    // Ici, on appellerait idéalement le Catalog Service pour restituer le stock
                }
            });
        }
    }
}
