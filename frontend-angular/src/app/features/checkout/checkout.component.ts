import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './checkout.component.html'
})
export class CheckoutComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  form = this.fb.nonNullable.group({
    adresseLivraison: [this.authService.currentUser()?.adresse ?? '', Validators.required],
    modePaiement: ['CARTE', Validators.required]
  });

  loading = false;
  errorMessage: string | null = null;

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    const userId = this.authService.currentUser()!.id;

    this.orderService
      .create({
        utilisateurId: userId,
        adresseLivraison: this.form.value.adresseLivraison!,
        modePaiement: this.form.value.modePaiement!
      })
      .subscribe({
        next: (commande) => this.router.navigate(['/paiement', commande.id]),
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.errorMessage =
            error.status === 400 || error.status === 409
              ? error.error?.message ?? 'Impossible de créer la commande (panier vide ?).'
              : 'Une erreur est survenue lors de la création de la commande.';
        }
      });
  }
}
