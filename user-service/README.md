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
- Swagger / OpenAPI
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
- [x] Swagger / OpenAPI (schéma Bearer JWT inclus)
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

Documentation interactive : `http://localhost:8081/swagger-ui.html`

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
- ✔ Swagger / OpenAPI

---

# Auteur

**Adama Seck**

Master 1 Systèmes d'Information

Projet : Plateforme E-Commerce à architecture Microservices

Université