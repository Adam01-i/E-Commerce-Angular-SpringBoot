import { Injectable } from '@angular/core';

/**
 * Porte l'action différée d'un Visiteur (ajouter au panier / commander) interceptée
 * faute d'authentification (cahier des charges 2.1.1) : l'action est rejouée
 * automatiquement une fois la connexion réussie, sans que l'utilisateur ait à la
 * relancer manuellement.
 */
@Injectable({ providedIn: 'root' })
export class PendingActionService {
  private pendingAction: (() => void) | null = null;
  private pendingReturnUrl: string | null = null;

  setPending(action: () => void, returnUrl: string): void {
    this.pendingAction = action;
    this.pendingReturnUrl = returnUrl;
  }

  consume(): { action: (() => void) | null; returnUrl: string | null } {
    const result = { action: this.pendingAction, returnUrl: this.pendingReturnUrl };
    this.pendingAction = null;
    this.pendingReturnUrl = null;
    return result;
  }
}
