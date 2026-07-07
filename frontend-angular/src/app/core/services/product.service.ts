import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config';
import { BulkPricing, Page, Product, ProductWriteRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly baseUrl = `${API_BASE_URL}/produits`;

  constructor(private http: HttpClient) {}

  list(page: number, size: number, includeHidden = false): Observable<Page<Product>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (includeHidden) {
      params = params.set('statut', 'ALL');
    }
    return this.http.get<Page<Product>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  search(query: string, includeHidden = false): Observable<Product[]> {
    let params = new HttpParams().set('q', query);
    if (includeHidden) {
      params = params.set('statut', 'ALL');
    }
    return this.http.get<Product[]>(`${this.baseUrl}/recherche`, { params });
  }

  filter(
    categoryId?: number,
    maxPrix?: number,
    minStock?: number,
    includeHidden = false
  ): Observable<Product[]> {
    let params = new HttpParams();
    if (categoryId !== undefined) params = params.set('categoryId', categoryId);
    if (maxPrix !== undefined) params = params.set('maxPrix', maxPrix);
    if (minStock !== undefined) params = params.set('minStock', minStock);
    if (includeHidden) params = params.set('statut', 'ALL');
    return this.http.get<Product[]>(`${this.baseUrl}/filtrer`, { params });
  }

  create(product: ProductWriteRequest): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product);
  }

  update(id: number, product: ProductWriteRequest): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  mask(id: number): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}/masquer`, {});
  }

  show(id: number): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}/afficher`, {});
  }

  restock(id: number, quantite: number): Observable<Product> {
    const params = new HttpParams().set('quantite', quantite);
    return this.http.put<Product>(`${this.baseUrl}/${id}/stock`, {}, { params });
  }

  getBulkPricings(productId: number): Observable<BulkPricing[]> {
    return this.http.get<BulkPricing[]>(`${this.baseUrl}/${productId}/prix-gros`);
  }

  addBulkPricing(productId: number, bulkPricing: Partial<BulkPricing>): Observable<BulkPricing> {
    return this.http.post<BulkPricing>(`${this.baseUrl}/${productId}/prix-gros`, bulkPricing);
  }

  updateBulkPricing(id: number, bulkPricing: Partial<BulkPricing>): Observable<BulkPricing> {
    return this.http.put<BulkPricing>(`${API_BASE_URL}/prix-gros/${id}`, bulkPricing);
  }

  deleteBulkPricing(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/prix-gros/${id}`);
  }

  activateBulkPricing(id: number): Observable<BulkPricing> {
    return this.http.put<BulkPricing>(`${API_BASE_URL}/prix-gros/${id}/activer`, {});
  }

  deactivateBulkPricing(id: number): Observable<BulkPricing> {
    return this.http.put<BulkPricing>(`${API_BASE_URL}/prix-gros/${id}/desactiver`, {});
  }
}
