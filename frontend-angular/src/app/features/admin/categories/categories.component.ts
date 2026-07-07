import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/models/models';
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [ReactiveFormsModule, AdminNavComponent],
  templateUrl: './categories.component.html'
})
export class AdminCategoriesComponent implements OnInit {
  private fb = inject(FormBuilder);

  categories: Category[] = [];
  loading = true;
  editingId: number | null = null;
  errorMessage: string | null = null;

  form = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    description: ['']
  });

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.categoryService.list().subscribe((categories) => {
      this.categories = categories;
      this.loading = false;
    });
  }

  edit(category: Category): void {
    this.editingId = category.id;
    this.form.setValue({ nom: category.nom, description: category.description ?? '' });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.form.reset();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.errorMessage = null;
    const value = this.form.getRawValue();

    const request$ = this.editingId
      ? this.categoryService.update(this.editingId, value)
      : this.categoryService.create(value);

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

  toggleStatus(category: Category): void {
    const request$ =
      category.statut === 'ACTIF'
        ? this.categoryService.mask(category.id)
        : this.categoryService.show(category.id);
    request$.subscribe(() => this.load());
  }

  remove(category: Category): void {
    if (!confirm(`Supprimer la catégorie "${category.nom}" ?`)) {
      return;
    }
    this.categoryService.delete(category.id).subscribe({
      next: () => this.load(),
      error: (error: HttpErrorResponse) => {
        alert(error.error?.message ?? 'Impossible de supprimer cette catégorie.');
      }
    });
  }
}
