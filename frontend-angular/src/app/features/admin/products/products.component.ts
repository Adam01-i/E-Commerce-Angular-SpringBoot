import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BulkPricing, Category, Product } from '../../../core/models/models';
import { resolveProductImageUrl } from '../../../core/utils/image-url';
import { CategoryService } from '../../../core/services/category.service';
import { ProductService } from '../../../core/services/product.service';
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AdminNavComponent],
  templateUrl: './products.component.html'
})
export class AdminProductsComponent implements OnInit {
  private fb = inject(FormBuilder);

  @ViewChild('fileInput') fileInput?: ElementRef<HTMLInputElement>;

  products: Product[] = [];
  categories: Category[] = [];
  loading = true;
  editingId: number | null = null;
  errorMessage: string | null = null;

  selectedFile: File | null = null;
  existingImageUrl: string | null = null;
  resolveImageUrl = resolveProductImageUrl;

  expandedProductId: number | null = null;
  bulkPricings: BulkPricing[] = [];

  bulkForm = this.fb.nonNullable.group({
    quantiteMinimale: [1, [Validators.required, Validators.min(1)]],
    prix: [0, [Validators.required, Validators.min(0)]]
  });

  form = this.fb.group({
    nom: ['', Validators.required],
    description: [''],
    prix: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    categoryId: [null as number | null, Validators.required]
  });

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.categoryService.list().subscribe((categories) => (this.categories = categories));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.productService.list(0, 1000, true).subscribe((page) => {
      this.products = page.content;
      this.loading = false;
    });
  }

  edit(product: Product): void {
    this.editingId = product.id;
    this.existingImageUrl = product.imagePrincipale ?? null;
    this.selectedFile = null;
    this.resetFileInput();
    this.form.setValue({
      nom: product.nom,
      description: product.description ?? '',
      prix: product.prix,
      stock: product.stock,
      categoryId: product.category?.id ?? null
    });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.existingImageUrl = null;
    this.selectedFile = null;
    this.resetFileInput();
    this.form.reset({ prix: 0, stock: 0, categoryId: null });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  private resetFileInput(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.errorMessage = null;
    const value = this.form.getRawValue();
    const payload = {
      nom: value.nom!,
      description: value.description ?? '',
      prix: value.prix!,
      stock: value.stock!,
      category: { id: value.categoryId! }
    };

    const request$ = this.editingId
      ? this.productService.update(this.editingId, payload, this.selectedFile)
      : this.productService.create(payload, this.selectedFile);

    request$.subscribe({
      next: () => {
        this.cancelEdit();
        this.load();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.error?.message ?? 'Une erreur est survenue.';
      }
    });
  }

  toggleStatus(product: Product): void {
    const request$ =
      product.statut === 'ACTIF' ? this.productService.mask(product.id) : this.productService.show(product.id);
    request$.subscribe(() => this.load());
  }

  remove(product: Product): void {
    if (!confirm(`Supprimer le produit "${product.nom}" ?`)) {
      return;
    }
    this.productService.delete(product.id).subscribe({
      next: () => this.load(),
      error: () => alert('Impossible de supprimer ce produit.')
    });
  }

  restock(product: Product): void {
    const quantite = Number(prompt('Quantité à ajouter au stock ?', '10'));
    if (!quantite || quantite <= 0) {
      return;
    }
    this.productService.restock(product.id, quantite).subscribe(() => this.load());
  }

  toggleBulkPricing(product: Product): void {
    if (this.expandedProductId === product.id) {
      this.expandedProductId = null;
      return;
    }
    this.expandedProductId = product.id;
    this.bulkForm.reset({ quantiteMinimale: 1, prix: 0 });
    this.productService.getBulkPricings(product.id).subscribe((pricings) => (this.bulkPricings = pricings));
  }

  addBulkPricing(product: Product): void {
    if (this.bulkForm.invalid) {
      this.bulkForm.markAllAsTouched();
      return;
    }
    this.productService
      .addBulkPricing(product.id, this.bulkForm.getRawValue() as Partial<BulkPricing>)
      .subscribe(() => this.toggleBulkPricingRefresh(product));
  }

  toggleBulkPricingStatus(pricing: BulkPricing, product: Product): void {
    const request$ =
      pricing.statut === 'ACTIF'
        ? this.productService.deactivateBulkPricing(pricing.id!)
        : this.productService.activateBulkPricing(pricing.id!);
    request$.subscribe(() => this.toggleBulkPricingRefresh(product));
  }

  removeBulkPricing(pricing: BulkPricing, product: Product): void {
    this.productService.deleteBulkPricing(pricing.id!).subscribe(() => this.toggleBulkPricingRefresh(product));
  }

  private toggleBulkPricingRefresh(product: Product): void {
    this.bulkForm.reset({ quantiteMinimale: 1, prix: 0 });
    this.productService.getBulkPricings(product.id).subscribe((pricings) => (this.bulkPricings = pricings));
  }
}
