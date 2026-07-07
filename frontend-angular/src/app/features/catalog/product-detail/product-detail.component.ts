import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/models';
import { ProductService } from '../../../core/services/product.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-detail.component.html'
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  quantite = 1;
  added = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getById(id).subscribe((product) => {
      this.product = product;
      this.loading = false;
    });
  }

  addToCart(): void {
    if (!this.product) {
      return;
    }
    const productId = this.product.id;
    const quantite = this.quantite;

    const doAdd = () => {
      const userId = this.authService.currentUser()!.id;
      this.cartService.addItem(userId, { produitId: productId, quantite }).subscribe(() => {
        this.added = true;
        setTimeout(() => (this.added = false), 1500);
      });
    };

    if (!this.authService.isAuthenticated()) {
      this.authService.redirectToLoginWithPendingAction(doAdd, this.router.url);
      return;
    }

    doAdd();
  }
}
