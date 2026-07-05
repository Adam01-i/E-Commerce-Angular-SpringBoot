# 🛒 E-Commerce-Angular-SpringBoot

## 📖 Présentation du projet

Ce projet est réalisé dans le cadre des modules :

- Développement Frontend avec Angular
- Développement Backend avec Spring Boot (IoC, Injection de Dépendances, Spring Data JPA, API REST, JWT, Microservices)

L'objectif est de concevoir et développer une plateforme **E-Commerce moderne** basée sur une **architecture Microservices**.

Chaque microservice est totalement indépendant et possède sa propre base de données PostgreSQL. Les communications entre les services sont réalisées via des API REST en utilisant **OpenFeign**, tandis que l'accès aux services est centralisé par une **API Gateway**.

Le frontend est développé avec **Angular** et communique uniquement avec l'API Gateway.

---

# 🏗️ Architecture générale du projet

```
E-COMMERCE-MICROSERVICES
│
├── api-gateway
├── user-service
├── catalog-service
├── order-service
├── payment-service
├── frontend-angular
├── docker-compose.yml
├── README.md
└── docs
```

---

# 📁 Description des dossiers

## 📌 api-gateway

Ce projet représente la porte d'entrée unique de toute l'application.

Son rôle est de :

- recevoir toutes les requêtes provenant du Frontend Angular
- vérifier les jetons JWT
- rediriger les requêtes vers le bon microservice
- centraliser les règles de sécurité
- gérer le CORS
- simplifier la communication entre le Frontend et les microservices

Aucun utilisateur ne communique directement avec un microservice.

Toutes les requêtes passent obligatoirement par l'API Gateway.

---

## 👤 user-service

Ce microservice est responsable de la gestion des utilisateurs.

Fonctionnalités :

- inscription
- connexion
- authentification JWT
- génération des Refresh Tokens
- modification du profil
- modification du mot de passe
- activation des comptes
- désactivation des comptes
- gestion des rôles
- sécurité Spring Security
- chiffrement des mots de passe avec BCrypt

Base de données :

```
userdb
```

---

## 📦 catalog-service

Ce microservice gère le catalogue.

Fonctionnalités :

- gestion des produits
- gestion des catégories
- gestion du stock
- gestion des images
- recherche
- filtrage
- prix de gros
- disponibilité des produits
- réservation temporaire du stock

Base de données :

```
catalogdb
```

---

## 🛍️ order-service

Ce microservice gère tout le processus de commande.

Fonctionnalités :

- panier
- ajout des produits
- suppression
- modification des quantités
- validation du panier
- création de commande
- historique
- génération des factures
- communication avec Catalog Service
- communication avec Payment Service

Base de données :

```
orderdb
```

---

## 💳 payment-service

Ce microservice gère les paiements.

Fonctions :

- paiement
- validation
- rejet
- changement du statut
- confirmation
- notification du Order Service

Base de données :

```
paymentdb
```

---

## 💻 frontend-angular

Application Web développée avec Angular.

Elle permet :

- consulter les produits
- rechercher
- filtrer
- gérer le panier
- passer une commande
- effectuer un paiement
- gérer le profil utilisateur
- administrer le catalogue
- administrer les utilisateurs

Le Frontend communique uniquement avec :

```
API Gateway
```

Jamais directement avec un microservice.

---

## 📄 docs

Ce dossier contient toute la documentation du projet.

Exemples :

- Cahier des charges
- Diagrammes UML
- Architecture
- Captures d'écran
- Documentation technique
- Documentation utilisateur
- Manuel d'installation

---

# 🗄️ Architecture des bases de données

Chaque microservice possède sa propre base PostgreSQL.

```
User Service
        │
     userdb

Catalog Service
        │
    catalogdb

Order Service
        │
      orderdb

Payment Service
        │
    paymentdb
```

Aucune base n'est partagée.

Les communications entre services passent uniquement par des API REST.

---

# 🔄 Flux de communication

```
Utilisateur

        │

Frontend Angular

        │

API Gateway

        │

 ├───────────────┐
 │               │
 │               │
 ▼               ▼
User         Catalog
 │               │
 └──────┐   ┌────┘
        ▼   ▼
      Order
        │
        ▼
    Payment
```

---

# 🛠️ Technologies utilisées

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- JWT
- BCrypt
- OpenFeign
- Resilience4J
- Bean Validation
- Lombok
- Swagger/OpenAPI

---

## Frontend

- Angular 20
- TypeScript
- HTML
- CSS
- Bootstrap

---

## Base de données

- PostgreSQL

---

## Conteneurisation

- Docker
- Docker Compose

---

## Outils

- IntelliJ IDEA
- VS Code
- Git
- GitHub
- Postman

---

# 👥 Répartition du travail

| Membre | Responsabilité |
|---------|----------------|
| **Adama Seck** | User Service |
| **Aissatou Diagne** | Catalog Service |
| **Arame Bacar Cissé** | Order Service |
| **Racine Sabaly** | Payment Service |
| **Abdoulaye Niang** | Frontend Angular + API Gateway + Intégration |

Chaque membre est responsable :

- du développement
- des tests
- de la documentation Swagger
- de la collection Postman
- de la conteneurisation Docker

de son microservice.

---

# 🚀 Organisation du développement

Le projet sera développé selon les étapes suivantes :

## Phase 1

- Création des projets
- Docker
- PostgreSQL
- Architecture

---

## Phase 2

Développement indépendant de chaque microservice.

Chaque membre travaille sur son service.

---

## Phase 3

Communication entre microservices.

- OpenFeign
- JWT
- API Gateway

---

## Phase 4

Développement complet du Frontend Angular.

Connexion avec les APIs.

---

## Phase 5

Tests complets

- Tests unitaires
- Tests d'intégration
- Tests fonctionnels

---

## Phase 6

Préparation de la soutenance.

- démonstration
- documentation
- présentation

---

# 📌 Bonnes pratiques

Tous les membres doivent respecter les règles suivantes :

- utiliser Git correctement
- créer une branche par fonctionnalité
- documenter les APIs avec Swagger
- tester les endpoints avec Postman
- écrire un code propre et lisible
- respecter les conventions de nommage
- effectuer des commits clairs et réguliers
- ne jamais accéder directement à la base d'un autre microservice
- toujours communiquer via API REST

---

# 🎯 Objectif final

Développer une plateforme E-Commerce moderne, sécurisée, modulaire et évolutive basée sur une architecture Microservices en utilisant Angular, Spring Boot, PostgreSQL et Docker, tout en appliquant les bonnes pratiques de développement logiciel et de travail collaboratif.