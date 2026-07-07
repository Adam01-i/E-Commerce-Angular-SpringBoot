import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { Commande } from '../../../core/models/models';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-list.component.html'
})
export class OrderListComponent implements OnInit {
  commandes: Commande[] = [];
  loading = true;

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.currentUser()!.id;
    this.orderService.listMine(userId).subscribe((commandes) => {
      this.commandes = commandes.sort(
        (a, b) => new Date(b.dateCommande).getTime() - new Date(a.dateCommande).getTime()
      );
      this.loading = false;
    });
  }
}
