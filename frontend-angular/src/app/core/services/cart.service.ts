import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, switchMap, tap } from 'rxjs';
import { API_BASE_URL } from '../config';
import { AjoutPanierRequest, Panier } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly baseUrl = `${API_BASE_URL}/panier`;

  /** Nombre d'articles du panier courant, utilisé pour le badge de la navbar. */
  readonly itemCount = signal(0);

  constructor(private http: HttpClient) {}

  get(utilisateurId: number): Observable<Panier> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http
      .get<Panier>(this.baseUrl, { params })
      .pipe(tap((panier) => this.updateCount(panier)));
  }

  addItem(utilisateurId: number, request: AjoutPanierRequest): Observable<Panier> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http
      .post<Panier>(`${this.baseUrl}/items`, request, { params })
      .pipe(tap((panier) => this.updateCount(panier)));
  }

  removeItem(utilisateurId: number, itemId: number): Observable<Panier> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    // Le DELETE renvoie 204 sans corps : on rafraîchit le panier pour retrouver le compte à jour.
    return this.http
      .delete<void>(`${this.baseUrl}/items/${itemId}`, { params })
      .pipe(switchMap(() => this.get(utilisateurId)));
  }

  resetCount(): void {
    this.itemCount.set(0);
  }

  private updateCount(panier: Panier): void {
    this.itemCount.set(panier.items?.reduce((total, item) => total + item.quantite, 0) ?? 0);
  }
}
