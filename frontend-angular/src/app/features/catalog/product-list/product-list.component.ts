import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { CategoryService } from '../../../core/services/category.service';
import { Category, Page, Product } from '../../../core/models/models';
import { ProductService } from '../../../core/services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  loading = true;

  // Mode "recherche/filtre" (liste plate) vs mode "parcours" (pagination serveur).
  searchMode = false;
  query = '';
  selectedCategoryId: number | null = null;
  maxPrix: number | null = null;

  page = 0;
  pageSize = 12;
  totalPages = 0;

  addedProductId: number | null = null;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.categoryService.list().subscribe((categories) => (this.categories = categories));

    this.route.queryParamMap.subscribe((params) => {
      this.query = params.get('q') ?? '';
      const categoryId = params.get('categoryId');
      this.selectedCategoryId = categoryId ? Number(categoryId) : null;
      this.page = 0;
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.searchMode = !!this.query || this.selectedCategoryId !== null || this.maxPrix !== null;

    if (this.query) {
      this.productService.search(this.query).subscribe((products) => this.onListLoaded(products));
    } else if (this.searchMode) {
      this.productService
        .filter(this.selectedCategoryId ?? undefined, this.maxPrix ?? undefined)
        .subscribe((products) => this.onListLoaded(products));
    } else {
      this.productService.list(this.page, this.pageSize).subscribe((result: Page<Product>) => {
        this.products = result.content;
        this.totalPages = result.totalPages;
        this.loading = false;
      });
    }
  }

  private onListLoaded(products: Product[]): void {
    this.products = products;
    this.totalPages = 1;
    this.loading = false;
  }

  applyFilters(): void {
    this.page = 0;
    this.load();
  }

  clearFilters(): void {
    this.selectedCategoryId = null;
    this.maxPrix = null;
    this.query = '';
    this.router.navigate(['/produits']);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) {
      return;
    }
    this.page = page;
    this.load();
  }

  addToCart(product: Product): void {
    const doAdd = () => {
      const userId = this.authService.currentUser()!.id;
      this.cartService.addItem(userId, { produitId: product.id, quantite: 1 }).subscribe(() => {
        this.addedProductId = product.id;
        setTimeout(() => (this.addedProductId = null), 1500);
      });
    };

    if (!this.authService.isAuthenticated()) {
      this.authService.redirectToLoginWithPendingAction(doAdd, this.router.url);
      return;
    }

    doAdd();
  }
}
