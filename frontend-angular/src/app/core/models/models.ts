// Modèles TypeScript alignés sur les DTOs exposés par les microservices via l'API Gateway.

export type Role = 'VISITEUR' | 'UTILISATEUR' | 'ADMIN';

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  adresse?: string;
  avatarUrl?: string;
  profil: Role;
  isActive: boolean;
  dateCreation?: string;
  dateModification?: string;
}

export interface JwtResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface RefreshTokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  telephone?: string;
  adresse?: string;
  avatarUrl?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  nom: string;
  prenom: string;
  telephone?: string;
  adresse?: string;
  avatarUrl?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// --- Catalogue ---

export interface Category {
  id: number;
  nom: string;
  description?: string;
  statut: 'ACTIF' | 'MASQUE';
}

export interface BulkPricing {
  id?: number;
  quantiteMinimale: number;
  prix: number;
  statut: 'ACTIF' | 'INACTIF';
}

export interface Product {
  id: number;
  nom: string;
  description?: string;
  prix: number;
  stock: number;
  statut: 'ACTIF' | 'MASQUE';
  imagePrincipale?: string;
  imagesSecondaires?: string;
  category: Category;
  prixGros: BulkPricing[];
  dateCreation?: string;
  dateModification?: string;
}

// Payload d'écriture (POST/PUT /produits) : la Category est référencée par id
// uniquement, comme attendu par la désérialisation JPA côté catalog-service.
export interface ProductWriteRequest {
  nom: string;
  description?: string;
  prix: number;
  stock: number;
  imagePrincipale?: string;
  imagesSecondaires?: string;
  category: { id: number };
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

// --- Panier / Commandes ---

export interface PanierItem {
  id: number;
  produitId: number;
  quantite: number;
  prixUnitaire: number;
}

export interface Panier {
  id: number;
  utilisateurId: number;
  dateCreation: string;
  items: PanierItem[];
}

export interface AjoutPanierRequest {
  produitId: number;
  quantite: number;
}

export type EtatCommande = 'EN_ATTENTE' | 'PAYEE' | 'EXPEDIEE' | 'LIVREE' | 'ANNULEE';

export interface LigneCommande {
  id: number;
  produitId: number;
  quantite: number;
  prixUnitaire: number;
}

export interface Commande {
  id: number;
  numeroCommande: string;
  utilisateurId: number;
  dateCommande: string;
  etat: EtatCommande;
  adresseLivraison: string;
  modePaiement: string;
  montantTotal: number;
  lignesCommande: LigneCommande[];
}

export interface CreerCommandeRequest {
  utilisateurId: number;
  adresseLivraison: string;
  modePaiement: string;
}

export interface Facture {
  id: number;
  commandeId: number;
  numeroFacture: string;
  dateEmission: string;
  montantTotal: number;
}

// --- Paiement ---

export type PaymentStatus = 'EN_ATTENTE' | 'VALIDE' | 'ECHOUE';

export interface Payment {
  id: number;
  commandeId: number;
  montant: number;
  modePaiement: string;
  statut: PaymentStatus;
  datePaiement: string;
}
