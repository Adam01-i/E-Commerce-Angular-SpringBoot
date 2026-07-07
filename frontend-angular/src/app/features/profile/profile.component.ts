import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);

  profileForm = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    telephone: [''],
    adresse: [''],
    avatarUrl: ['']
  });

  passwordForm = this.fb.nonNullable.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  profileSaved = false;
  profileError: string | null = null;
  passwordSaved = false;
  passwordError: string | null = null;

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.authService.fetchCurrentUser().subscribe((user) => {
      this.profileForm.patchValue({
        nom: user.nom,
        prenom: user.prenom,
        telephone: user.telephone,
        adresse: user.adresse,
        avatarUrl: user.avatarUrl
      });
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.profileError = null;
    this.authService.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: () => {
        this.profileSaved = true;
        setTimeout(() => (this.profileSaved = false), 2000);
      },
      error: () => (this.profileError = 'Impossible de mettre à jour le profil.')
    });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }
    this.passwordError = null;
    this.authService.changePassword(this.passwordForm.getRawValue() as {
      oldPassword: string;
      newPassword: string;
    }).subscribe({
      next: () => {
        this.passwordSaved = true;
        this.passwordForm.reset();
        setTimeout(() => (this.passwordSaved = false), 2000);
      },
      error: (error: HttpErrorResponse) => {
        this.passwordError =
          error.status === 400
            ? "L'ancien mot de passe est incorrect."
            : 'Impossible de modifier le mot de passe.';
      }
    });
  }
}
