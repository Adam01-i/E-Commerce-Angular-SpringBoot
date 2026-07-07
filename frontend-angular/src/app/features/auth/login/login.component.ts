import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private fb = inject(FormBuilder);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = false;
  errorMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? undefined;

    this.authService
      .login(this.form.getRawValue() as { email: string; password: string }, returnUrl)
      .subscribe({
      next: () => (this.loading = false),
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage =
          error.status === 401
            ? 'Email ou mot de passe incorrect.'
            : "Une erreur est survenue, veuillez réessayer.";
      }
    });
  }
}
