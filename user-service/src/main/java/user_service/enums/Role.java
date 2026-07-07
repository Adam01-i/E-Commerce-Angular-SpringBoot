package user_service.enums;

/**
 * Enumération des rôles applicatifs gérés par la plateforme.
 *
 * Conformément au cahier des charges, le rôle n'est PAS stocké dans une table
 * dédiée : il est persisté directement dans la colonne "profil" de la table
 * "users", sous forme de chaîne de caractères (nom de l'enum).
 *
 * - VISITEUR   : rôle implicite, jamais persisté (aucun compte n'est créé pour
 *                un simple visiteur non authentifié). Conservé ici uniquement
 *                pour la cohérence du modèle de rôles côté sécurité.
 * - UTILISATEUR: rôle par défaut attribué à tout compte créé via /register.
 * - ADMIN      : rôle disposant des droits d'administration de la plateforme.
 */
public enum Role {
    VISITEUR,
    UTILISATEUR,
    ADMIN
}
