import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { Panier, Product } from '../../core/models/models';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  panier: Panier | null = null;
  productsById = new Map<number, Product>();
  loading = true;

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading = true;
    const userId = this.authService.currentUser()!.id;
    this.cartService.get(userId).subscribe((panier) => {
      this.panier = panier;
      this.loadProductDetails();
    });
  }

  private loadProductDetails(): void {
    const items = this.panier?.items ?? [];
    if (items.length === 0) {
      this.loading = false;
      return;
    }

    const requests = items.map((item) => this.productService.getById(item.produitId));
    forkJoin(requests).subscribe((products) => {
      this.productsById = new Map(products.map((product) => [product.id, product]));
      this.loading = false;
    });
  }

  removeItem(itemId: number): void {
    const userId = this.authService.currentUser()!.id;
    this.cartService.removeItem(userId, itemId).subscribe((panier) => {
      this.panier = panier;
      this.loadProductDetails();
    });
  }

  get total(): number {
    return (this.panier?.items ?? []).reduce(
      (sum, item) => sum + item.quantite * item.prixUnitaire,
      0
    );
  }
}
