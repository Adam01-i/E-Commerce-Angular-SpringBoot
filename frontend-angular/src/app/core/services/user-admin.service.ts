import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config';
import { User } from '../models/models';

/** Réservé ADMIN : gestion des comptes utilisateurs (cahier des charges 2.1.3.e). */
@Injectable({ providedIn: 'root' })
export class UserAdminService {
  private readonly baseUrl = `${API_BASE_URL}/users`;

  constructor(private http: HttpClient) {}

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  activate(id: number): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}/activate`, {});
  }

  deactivate(id: number): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}/deactivate`, {});
  }
}
