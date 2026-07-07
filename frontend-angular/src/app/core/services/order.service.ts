import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config';
import { Commande, CreerCommandeRequest, EtatCommande, Facture } from '../models/models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly baseUrl = `${API_BASE_URL}/commandes`;

  constructor(private http: HttpClient) {}

  create(request: CreerCommandeRequest): Observable<Commande> {
    return this.http.post<Commande>(this.baseUrl, request);
  }

  getById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.baseUrl}/${id}`);
  }

  listMine(utilisateurId: number): Observable<Commande[]> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http.get<Commande[]>(this.baseUrl, { params });
  }

  listAll(): Observable<Commande[]> {
    return this.http.get<Commande[]>(`${this.baseUrl}/admin`);
  }

  updateStatus(id: number, etat: EtatCommande): Observable<Commande> {
    return this.http.put<Commande>(`${this.baseUrl}/${id}/statut`, { etat });
  }

  getInvoice(commandeId: number): Observable<Facture> {
    return this.http.get<Facture>(`${API_BASE_URL}/factures/${commandeId}`);
  }

  history(utilisateurId: number): Observable<Commande[]> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http.get<Commande[]>(`${API_BASE_URL}/historique`, { params });
  }
}
