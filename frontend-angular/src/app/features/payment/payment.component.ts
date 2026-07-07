import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Commande, Payment } from '../../core/models/models';
import { OrderService } from '../../core/services/order.service';
import { PaymentService } from '../../core/services/payment.service';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './payment.component.html'
})
export class PaymentComponent implements OnInit {
  commande: Commande | null = null;
  payment: Payment | null = null;
  loading = true;
  processing = false;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    const commandeId = Number(this.route.snapshot.paramMap.get('commandeId'));

    this.orderService.getById(commandeId).subscribe((commande) => (this.commande = commande));

    this.paymentService.getByCommande(commandeId).subscribe((payments) => {
      // Un seul paiement "EN_ATTENTE" par commande à un instant donné (côté payment-service).
      this.payment = payments.find((p) => p.statut === 'EN_ATTENTE') ?? payments.at(-1) ?? null;
      this.loading = false;
    });
  }

  /**
   * Aucun prestataire de paiement réel n'est intégré (hors périmètre du projet) : le
   * bouton simule la réponse d'une passerelle de paiement pour permettre de dérouler
   * le parcours complet jusqu'à la facture.
   */
  simulate(outcome: 'validate' | 'reject'): void {
    if (!this.payment) {
      return;
    }
    this.processing = true;
    const action$ =
      outcome === 'validate'
        ? this.paymentService.validate(this.payment.id)
        : this.paymentService.reject(this.payment.id);

    action$.subscribe((payment) => {
      this.payment = payment;
      this.processing = false;
    });
  }
}
