import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_ENDPOINTS = ['/auth/login', '/auth/register', '/auth/refresh-token'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const isAuthEndpoint = AUTH_ENDPOINTS.some((endpoint) => req.url.includes(endpoint));

  const token = authService.accessToken;
  const authorizedReq = token && !isAuthEndpoint
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authorizedReq).pipe(
    catchError((error: unknown) => {
      const isUnauthorized = error instanceof HttpErrorResponse && error.status === 401;
      if (!isUnauthorized || isAuthEndpoint || !authService.refreshToken) {
        return throwError(() => error);
      }

      // Le jeton d'accès a expiré : on tente un unique rafraîchissement avant
      // de rejouer la requête, sans quoi l'utilisateur est déconnecté.
      return authService.refreshAccessToken().pipe(
        switchMap(() => {
          const retriedReq = req.clone({
            setHeaders: { Authorization: `Bearer ${authService.accessToken}` }
          });
          return next(retriedReq);
        }),
        catchError((refreshError: unknown) => {
          authService.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
