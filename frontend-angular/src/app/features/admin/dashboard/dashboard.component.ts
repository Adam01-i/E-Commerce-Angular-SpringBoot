import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Commande, Payment, Product, User } from '../../../core/models/models';
import { OrderService } from '../../../core/services/order.service';
import { PaymentService } from '../../../core/services/payment.service';
import { ProductService } from '../../../core/services/product.service';
import { UserAdminService } from '../../../core/services/user-admin.service';
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, AdminNavComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  loading = true;

  produitsActifs = 0;
  produitsMasques = 0;

  commandesParEtat: Record<string, number> = {};
  chiffreAffairesTotal = 0;

  utilisateursActifs = 0;
  utilisateursDesactives = 0;

  paiementsReussis = 0;
  montantPaiementsReussis = 0;

  constructor(
    private productService: ProductService,
    private orderService: OrderService,
    private userAdminService: UserAdminService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    forkJoin({
      produits: this.productService.list(0, 1000, true),
      commandes: this.orderService.listAll(),
      utilisateurs: this.userAdminService.list(),
      paiements: this.paymentService.listAll()
    }).subscribe(({ produits, commandes, utilisateurs, paiements }) => {
      this.computeProduits(produits.content);
      this.computeCommandes(commandes);
      this.computeUtilisateurs(utilisateurs);
      this.computePaiements(paiements);
      this.loading = false;
    });
  }

  private computeProduits(produits: Product[]): void {
    this.produitsActifs = produits.filter((p) => p.statut === 'ACTIF').length;
    this.produitsMasques = produits.filter((p) => p.statut === 'MASQUE').length;
  }

  private computeCommandes(commandes: Commande[]): void {
    this.commandesParEtat = commandes.reduce<Record<string, number>>((acc, commande) => {
      acc[commande.etat] = (acc[commande.etat] ?? 0) + 1;
      return acc;
    }, {});
    this.chiffreAffairesTotal = commandes
      .filter((c) => c.etat !== 'ANNULEE')
      .reduce((sum, c) => sum + c.montantTotal, 0);
  }

  private computeUtilisateurs(utilisateurs: User[]): void {
    this.utilisateursActifs = utilisateurs.filter((u) => u.isActive).length;
    this.utilisateursDesactives = utilisateurs.filter((u) => !u.isActive).length;
  }

  private computePaiements(paiements: Payment[]): void {
    const reussis = paiements.filter((p) => p.statut === 'VALIDE');
    this.paiementsReussis = reussis.length;
    this.montantPaiementsReussis = reussis.reduce((sum, p) => sum + p.montant, 0);
  }

  get totalUtilisateurs(): number {
    return this.utilisateursActifs + this.utilisateursDesactives;
  }

  get totalCommandes(): number {
    return Object.values(this.commandesParEtat).reduce((a, b) => a + b, 0);
  }

  readonly etats: Commande['etat'][] = ['EN_ATTENTE', 'PAYEE', 'EXPEDIEE', 'LIVREE', 'ANNULEE'];
}
