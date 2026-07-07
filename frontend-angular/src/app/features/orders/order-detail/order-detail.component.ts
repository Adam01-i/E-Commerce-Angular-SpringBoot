import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Commande, Facture } from '../../../core/models/models';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-detail.component.html'
})
export class OrderDetailComponent implements OnInit {
  commande: Commande | null = null;
  facture: Facture | null = null;
  loading = true;
  factureError = false;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getById(id).subscribe((commande) => {
      this.commande = commande;
      this.loading = false;

      if (commande.etat !== 'EN_ATTENTE') {
        this.orderService.getInvoice(id).subscribe({
          next: (facture) => (this.facture = facture),
          error: () => (this.factureError = true)
        });
      }
    });
  }
}
