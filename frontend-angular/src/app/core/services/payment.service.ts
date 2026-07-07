import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config';
import { Payment } from '../models/models';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly baseUrl = `${API_BASE_URL}/payments`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.baseUrl}/${id}`);
  }

  getByCommande(commandeId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.baseUrl}/commande/${commandeId}`);
  }

  validate(id: number): Observable<Payment> {
    return this.http.put<Payment>(`${this.baseUrl}/${id}/validate`, {});
  }

  reject(id: number): Observable<Payment> {
    return this.http.put<Payment>(`${this.baseUrl}/${id}/reject`, {});
  }

  /** Réservé admin (tableau de bord) : liste l'ensemble des paiements. */
  listAll(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.baseUrl);
  }
}
