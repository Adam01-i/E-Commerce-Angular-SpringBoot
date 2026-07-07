import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Commande, EtatCommande } from '../../../core/models/models';
import { OrderService } from '../../../core/services/order.service';
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

const TRANSITIONS: Record<EtatCommande, EtatCommande[]> = {
  EN_ATTENTE: ['PAYEE', 'ANNULEE'],
  PAYEE: ['EXPEDIEE', 'ANNULEE'],
  EXPEDIEE: ['LIVREE'],
  LIVREE: [],
  ANNULEE: []
};

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, AdminNavComponent],
  templateUrl: './orders.component.html'
})
export class AdminOrdersComponent implements OnInit {
  commandes: Commande[] = [];
  loading = true;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.orderService.listAll().subscribe((commandes) => {
      this.commandes = commandes.sort(
        (a, b) => new Date(b.dateCommande).getTime() - new Date(a.dateCommande).getTime()
      );
      this.loading = false;
    });
  }

  availableTransitions(commande: Commande): EtatCommande[] {
    return TRANSITIONS[commande.etat];
  }

  changeStatus(commande: Commande, etat: EtatCommande): void {
    this.orderService.updateStatus(commande.id, etat).subscribe(() => this.load());
  }
}
