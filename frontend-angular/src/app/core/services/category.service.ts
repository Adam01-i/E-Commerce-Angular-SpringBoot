import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config';
import { Category } from '../models/models';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly baseUrl = `${API_BASE_URL}/categories`;

  constructor(private http: HttpClient) {}

  list(): Observable<Category[]> {
    return this.http.get<Category[]>(this.baseUrl);
  }

  create(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.baseUrl, category);
  }

  update(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.baseUrl}/${id}`, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  mask(id: number): Observable<Category> {
    return this.http.put<Category>(`${this.baseUrl}/${id}/masquer`, {});
  }

  show(id: number): Observable<Category> {
    return this.http.put<Category>(`${this.baseUrl}/${id}/afficher`, {});
  }
}
