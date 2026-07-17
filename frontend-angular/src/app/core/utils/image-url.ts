import { API_ORIGIN } from '../config';

// imagePrincipale peut être une URL absolue héritée (anciens produits saisis
// manuellement) ou un chemin relatif renvoyé par catalog-service pour les
// images désormais uploadées (ex. "/api/produits/images/xxx.jpg").
export function resolveProductImageUrl(path: string | null | undefined, fallback: string): string {
  if (!path) {
    return fallback;
  }
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  return `${API_ORIGIN}${path}`;
}
