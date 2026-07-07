import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config';
import {
  ChangePasswordRequest,
  JwtResponse,
  LoginRequest,
  RefreshTokenResponse,
  RegisterRequest,
  UpdateProfileRequest,
  User
} from '../models/models';
import { PendingActionService } from './pending-action.service';

const ACCESS_TOKEN_KEY = 'ecommerce_access_token';
const REFRESH_TOKEN_KEY = 'ecommerce_refresh_token';
const USER_KEY = 'ecommerce_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<User | null>(this.readStoredUser());

  constructor(
    private http: HttpClient,
    private router: Router,
    private pendingActionService: PendingActionService
  ) {}

  get accessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  get refreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.accessToken !== null && this.currentUser() !== null;
  }

  isAdmin(): boolean {
    return this.currentUser()?.profil === 'ADMIN';
  }

  login(credentials: LoginRequest, fallbackReturnUrl?: string): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${API_BASE_URL}/auth/login`, credentials).pipe(
      tap((response) => this.storeSession(response, fallbackReturnUrl))
    );
  }

  register(request: RegisterRequest, fallbackReturnUrl?: string): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${API_BASE_URL}/auth/register`, request).pipe(
      tap((response) => this.storeSession(response, fallbackReturnUrl))
    );
  }

  refreshAccessToken(): Observable<RefreshTokenResponse> {
    return this.http
      .post<RefreshTokenResponse>(`${API_BASE_URL}/auth/refresh-token`, { refreshToken: this.refreshToken })
      .pipe(tap((response) => localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken)));
  }

  fetchCurrentUser(): Observable<User> {
    return this.http.get<User>(`${API_BASE_URL}/users/me`).pipe(
      tap((user) => {
        this.currentUser.set(user);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
      })
    );
  }

  updateProfile(request: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${API_BASE_URL}/users/me`, request).pipe(
      tap((user) => {
        this.currentUser.set(user);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
      })
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<User> {
    return this.http.put<User>(`${API_BASE_URL}/users/me/password`, request);
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/']);
  }

  /**
   * Redirige un Visiteur vers la connexion en mémorisant l'action qu'il souhaitait
   * effectuer (ajouter au panier, commander...), rejouée automatiquement après
   * une connexion réussie (cahier des charges 2.1.1).
   */
  redirectToLoginWithPendingAction(action: () => void, returnUrl: string): void {
    this.pendingActionService.setPending(action, returnUrl);
    this.router.navigate(['/login']);
  }

  private storeSession(response: JwtResponse, fallbackReturnUrl?: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    this.currentUser.set(response.user);

    // Priorité à l'action différée (ajout au panier interrompu), sinon à la page
    // que le guard tentait d'ouvrir (returnUrl), sinon l'accueil.
    const { action, returnUrl } = this.pendingActionService.consume();
    if (action) {
      action();
    }
    this.router.navigateByUrl(returnUrl ?? fallbackReturnUrl ?? '/');
  }

  private readStoredUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  }
}
