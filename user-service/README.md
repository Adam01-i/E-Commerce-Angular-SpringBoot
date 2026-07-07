# User/Auth Service

Microservice responsable de la gestion des utilisateurs, de l'authentification et de l'autorisation de la plateforme E-Commerce.

Développeur : **Adama Seck**

---

# Objectif

Ce microservice est chargé de :

- Inscription des utilisateurs
- Authentification (JWT)
- Gestion des rôles
- Gestion du profil
- Gestion des mots de passe
- Vérification des comptes
- Communication avec les autres microservices (OpenFeign) — prévue lors de la phase d'intégration.

Conforme au cahier des charges du projet de Master.

---

# Technologies utilisées

- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt)
- BCrypt
- Bean Validation
- Maven
- Docker
- Lombok
- ModelMapper

---

# Architecture

Le projet suit une architecture en couches :

```
Controller
    │
Service (interface) → Service.impl
    │
Repository
    │
PostgreSQL
```

Packages principaux :

```
config/
controller/
dto/
entity/
enums/
repository/
security/
service/
service/impl/
exception/
mapper/
util/
```

---

# Base de données

Base dédiée :

```
userdb
```

Principe appliqué :

> Database per Service

Aucun autre microservice n'accède directement à cette base. Une seule table est utilisée : `users` (aucune table Address ni Role séparée — le rôle est stocké dans la colonne `profil`).

---

# État d'avancement

## Étape 1 — Initialisation du projet

### Réalisé

- [x] Création du projet Spring Boot
- [x] Configuration Maven
- [x] Java 21
- [x] Spring Boot 3.5.x
- [x] Spring Data JPA
- [x] Spring Security
- [x] Bean Validation
- [x] PostgreSQL Driver
- [x] Lombok
- [x] JWT
- [x] ModelMapper
- [x] Dockerfile
- [x] Connexion PostgreSQL
- [x] Structure initiale du projet
- [x] Démarrage du projet sans erreur

## Étape 2 — Cœur métier

### Réalisé

- [x] Entité JPA `User` (mappée sur le schéma exact imposé)
- [x] Enum `Role` (VISITEUR / UTILISATEUR / ADMIN)
- [x] Repository (`findByEmail`, `existsByEmail`, `findByIsActive`)
- [x] DTO complets (Register, Login, UpdateProfile, ChangePassword, Jwt, RefreshToken x2, UserResponse, ErrorResponse)
- [x] Mapper Entity ↔ DTO (ModelMapper)
- [x] Services (interfaces + implémentations) : `AuthService`, `UserService`
- [x] Authentification JWT (access token + refresh token stateless)
- [x] Spring Security (`SecurityFilterChain`, pas de `WebSecurityConfigurerAdapter`)
- [x] BCrypt (chiffrement des mots de passe)
- [x] Gestion centralisée des exceptions (`GlobalExceptionHandler` + 5 exceptions métier)
- [x] Verrouillage temporaire après échecs de connexion répétés
- [x] Endpoints REST complets (`/api/auth/**`, `/api/users/**`)

### En cours

- [ ] Tests unitaires (services, sécurité)
- [ ] Tests Postman (collection à exporter et versionner)
- [ ] Intégration avec l'API Gateway

---

# Structure actuelle

```
user-service/
│
├── src/
│   ├── main/java/user_service/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   ├── service/impl/
│   │   ├── util/
│   │   └── UserServiceApplication.java
│   └── test/java/user_service/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
└── src/main/resources/application.yml
```

---

# Endpoints disponibles

| Méthode | URL | Accès |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| POST | /api/auth/refresh-token | Public |
| GET | /api/users/me | Authentifié |
| PUT | /api/users/me | Authentifié |
| PUT | /api/users/me/password | Authentifié |
| GET | /api/users | ADMIN |
| GET | /api/users/{id} | ADMIN |
| PUT | /api/users/{id}/activate | ADMIN |
| PUT | /api/users/{id}/deactivate | ADMIN |


---

# Lancer le projet

```bash
docker-compose up --build
```

Le service démarre sur le port `8081`, la base PostgreSQL dédiée sur le port `5433` (hôte) / `5432` (conteneur).

---

# Cahier des charges respecté

- ✔ Java 21
- ✔ Spring Boot 3
- ✔ Maven
- ✔ PostgreSQL
- ✔ Architecture par couches
- ✔ Database per Service
- ✔ Docker
- ✔ Spring Security (SecurityFilterChain)
- ✔ JWT (access + refresh)
- ✔ BCrypt
- ✔ Bean Validation
- ✔ Gestion centralisée des exceptions

---

---

# Documentation détaillée des endpoints

Le microservice expose deux grandes catégories d'API :

- **API publiques** : accessibles sans authentification.
- **API protégées** : accessibles uniquement avec un jeton JWT valide. Certaines sont réservées aux administrateurs.

---

## 1. Inscription d'un utilisateur

### Endpoint

```http
POST /api/auth/register
```

### Accès

Public

### Description

Permet à un visiteur de créer un nouveau compte utilisateur.

Lors de l'inscription :

- le mot de passe est chiffré avec BCrypt ;
- le rôle par défaut est **UTILISATEUR** ;
- le compte est activé ;
- un Access Token et un Refresh Token sont immédiatement générés.

### Corps de la requête

```json
{
    "nom": "Seck",
    "prenom": "Adama",
    "email": "adama@gmail.com",
    "password": "Passer123",
    "telephone": "771234567",
    "adresse": "Dakar",
    "avatarUrl": ""
}
```

### Réponse

```
201 Created
```

Retourne :

- Access Token
- Refresh Token
- Type du token
- Durée d'expiration
- Informations de l'utilisateur

---

## 2. Authentification (Connexion)

### Endpoint

```http
POST /api/auth/login
```

### Accès

Public

### Description

Authentifie un utilisateur à partir de son adresse e-mail et de son mot de passe.

Après authentification :

- vérification du mot de passe avec BCrypt ;
- génération d'un nouvel Access Token ;
- génération d'un Refresh Token.

### Corps de la requête

```json
{
    "email": "adama@gmail.com",
    "password": "Passer123"
}
```

### Réponse

```
200 OK
```

---

## 3. Rafraîchissement du JWT

### Endpoint

```http
POST /api/auth/refresh-token
```

### Accès

Public

### Description

Permet d'obtenir un nouveau Access Token sans demander une nouvelle authentification.

Le Refresh Token doit être valide et non expiré.

### Corps de la requête

```json
{
    "refreshToken": "..."
}
```

### Réponse

```
200 OK
```

Retourne un nouvel Access Token.

---

## 4. Consultation de son profil

### Endpoint

```http
GET /api/users/me
```

### Accès

Utilisateur authentifié

### Description

Retourne les informations du compte associé au JWT fourni dans l'en-tête Authorization.

### Header

```text
Authorization: Bearer <access_token>
```

### Réponse

```
200 OK
```

---

## 5. Modification de son profil

### Endpoint

```http
PUT /api/users/me
```

### Accès

Utilisateur authentifié

### Description

Permet de modifier les informations personnelles :

- nom
- prénom
- téléphone
- adresse
- avatar

Le rôle et l'adresse e-mail ne peuvent pas être modifiés via cette route.

### Réponse

```
200 OK
```

---

## 6. Changement du mot de passe

### Endpoint

```http
PUT /api/users/me/password
```

### Accès

Utilisateur authentifié

### Description

Permet de modifier son mot de passe.

L'ancien mot de passe doit obligatoirement être fourni.

Le nouveau mot de passe est automatiquement chiffré avant d'être enregistré.

### Réponse

```
200 OK
```

---

## 7. Liste de tous les utilisateurs

### Endpoint

```http
GET /api/users
```

### Accès

Administrateur uniquement

### Description

Retourne la liste complète des utilisateurs enregistrés.

### Réponse

```
200 OK
```

---

## 8. Consultation d'un utilisateur

### Endpoint

```http
GET /api/users/{id}
```

### Accès

Administrateur uniquement

### Description

Retourne les informations d'un utilisateur à partir de son identifiant.

### Réponse

```
200 OK
```

ou

```
404 Not Found
```

---

## 9. Activation d'un compte

### Endpoint

```http
PUT /api/users/{id}/activate
```

### Accès

Administrateur uniquement

### Description

Active un compte utilisateur.

L'utilisateur pourra alors accéder à l'application.

### Réponse

```
200 OK
```

---

## 10. Désactivation d'un compte

### Endpoint

```http
PUT /api/users/{id}/deactivate
```

### Accès

Administrateur uniquement

### Description

Désactive un compte utilisateur.

L'utilisateur ne pourra plus se connecter.

### Réponse

```
200 OK
```

---

# Sécurité de l'API

L'ensemble des endpoints protégés utilisent **JWT (JSON Web Token)**.

Le client doit transmettre le jeton d'accès dans chaque requête :

```text
Authorization: Bearer <access_token>
```

Les routes sont protégées selon les règles suivantes :

| Endpoint | Authentification | Rôle requis |
|-----------|-----------------|-------------|
| /api/auth/** | Non | Aucun |
| /api/users/me | Oui | UTILISATEUR ou ADMIN |
| /api/users/me/password | Oui | UTILISATEUR ou ADMIN |
| /api/users | Oui | ADMIN |
| /api/users/{id} | Oui | ADMIN |
| /api/users/{id}/activate | Oui | ADMIN |
| /api/users/{id}/deactivate | Oui | ADMIN |

---

# Tests fonctionnels réalisés

Les fonctionnalités suivantes ont été entièrement testées avec Postman :

- ✅ Inscription d'un utilisateur
- ✅ Authentification (connexion)
- ✅ Génération de l'Access Token
- ✅ Génération du Refresh Token
- ✅ Rafraîchissement du JWT
- ✅ Consultation du profil utilisateur
- ✅ Modification du profil
- ✅ Changement du mot de passe
- ✅ Authentification JWT
- ✅ Contrôle des rôles (UTILISATEUR / ADMIN)
- ✅ Protection des endpoints
- ✅ Activation d'un compte utilisateur
- ✅ Désactivation d'un compte utilisateur
- ✅ Gestion des erreurs HTTP (401, 403 et 404)

L'ensemble de ces tests a été validé avec succès à l'aide de Postman.

---

# Auteur

**Adama Seck**

Master 1 Systèmes d'Information

Projet : Plateforme E-Commerce à architecture Microservices

Université