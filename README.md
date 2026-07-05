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

# 🌿 Branches Git du projet

Afin de faciliter le travail collaboratif, chaque membre développera ses fonctionnalités sur une branche dédiée avant leur fusion dans les branches principales.

## Structure des branches

```text
main
develop

feature/user-service
feature/catalog-service
feature/order-service
feature/payment-service
feature/frontend
```

## Répartition des branches

| Membre | Branche |
|---------|----------|
| **Adama Seck** | `feature/user-service` |
| **Aissatou Diagne** | `feature/catalog-service` |
| **Arame Bacar Cissé** | `feature/order-service` |
| **Racine Sabaly** | `feature/payment-service` |
| **Abdoulaye Niang** | `feature/frontend` |

### Workflow Git

Chaque membre devra :

1. Créer sa branche à partir de `develop`.
2. Développer ses fonctionnalités.
3. Effectuer des commits réguliers.
4. Pousser ses modifications sur GitHub.
5. Fusionner sa branche dans `develop` après validation.
6. Une fois toutes les fonctionnalités validées, la branche `develop` sera fusionnée dans `main`.

---

# 🏗️ Architecture générale du projet

L'organisation du dépôt Git sera la suivante :

```text
E-COMMERCE-MICROSERVICES
│
├── api-gateway
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src
│   └── ...
│
├── user-service
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src
│   └── ...
│
├── catalog-service
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src
│   └── ...
│
├── order-service
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src
│   └── ...
│
├── payment-service
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src
│   └── ...
│
├── frontend-angular
│   ├── package.json
│   ├── angular.json
│   ├── src
│   └── ...
│
├── docs
│
├── docker-compose.yml
│
└── README.md
```

## 📂 Description de l'architecture

- **api-gateway** : Point d'entrée unique de l'application. Toutes les requêtes du Frontend transitent par cette passerelle avant d'être redirigées vers le microservice concerné.

- **user-service** : Gère les utilisateurs, l'authentification, l'autorisation, les rôles, les profils ainsi que la sécurité basée sur JWT.

- **catalog-service** : Gère les produits, les catégories, les stocks, les images et les prix de gros.

- **order-service** : Gère le panier, les commandes, les factures et la communication avec les autres microservices pour le traitement des achats.

- **payment-service** : Gère les paiements, la validation des transactions et la mise à jour des statuts des commandes.

- **frontend-angular** : Application Angular consommant les API exposées via l'API Gateway.

- **docs** : Contient l'ensemble de la documentation du projet (cahier des charges, diagrammes UML, captures d'écran, documentation technique, etc.).

- **docker-compose.yml** : Permet de lancer l'ensemble des services (microservices, bases PostgreSQL, API Gateway et Frontend) à l'aide d'une seule commande Docker Compose.

- **README.md** : Documentation principale du projet expliquant son architecture, son fonctionnement et les consignes de développement.

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

# 🐳 Stratégie de développement avec Docker

Afin de garantir un environnement de développement identique pour tous les membres de l'équipe, le projet sera conteneurisé dès le début du développement.

Chaque microservice disposera de son propre **Dockerfile**, tandis qu'un unique fichier **docker-compose.yml** situé à la racine du projet permettra de lancer l'ensemble de l'architecture (API Gateway, microservices, bases de données PostgreSQL et Frontend Angular).

Tous les services communiqueront via un **réseau interne Docker** en utilisant le nom des conteneurs (ex. `http://catalog-service:8082`) plutôt que des adresses IP ou `localhost`.

Cette approche garantit :

- Un environnement identique pour tous les développeurs.
- Une intégration simplifiée entre les microservices.
- Une configuration centralisée grâce à Docker Compose.
- Un déploiement rapide de toute l'application avec une seule commande :

```bash
docker compose up -d
```

L'ensemble du projet sera donc développé, testé et intégré directement dans son environnement Docker afin d'éviter les problèmes de configuration lors de la phase finale d'intégration.

---

## 🌐 Ports utilisés par les services

Afin d'éviter les conflits de ports et de faciliter le développement, chaque service de l'architecture dispose de son propre port.

| Service | Port | Description |
|---------|:----:|-------------|
| **API Gateway** | **8080** | Point d'entrée unique de toutes les requêtes du Frontend vers les microservices. |
| **User Service** | **8081** | Gestion des utilisateurs, de l'authentification et de l'autorisation (JWT). |
| **Catalog Service** | **8082** | Gestion des produits, catégories, stocks et prix de gros. |
| **Order Service** | **8083** | Gestion des paniers, commandes et factures. |
| **Payment Service** | **8084** | Gestion des paiements et validation des transactions. |
| **Frontend Angular** | **4200** | Interface utilisateur de l'application E-Commerce. |
| **PostgreSQL - User** | **5433** | Base de données dédiée au **User Service** (port exposé sur la machine hôte). |
| **PostgreSQL - Catalog** | **5434** | Base de données dédiée au **Catalog Service** (port exposé sur la machine hôte). |
| **PostgreSQL - Order** | **5435** | Base de données dédiée au **Order Service** (port exposé sur la machine hôte). |
| **PostgreSQL - Payment** | **5436** | Base de données dédiée au **Payment Service** (port exposé sur la machine hôte). |

> **Remarque :** À l'intérieur du réseau Docker, chaque conteneur PostgreSQL continue d'utiliser son port standard **5432**. Les ports **5433**, **5434**, **5435** et **5436** sont uniquement exposés sur la machine hôte afin de permettre l'accès aux bases de données via des outils comme **DBeaver**, **pgAdmin** ou **IntelliJ IDEA**.

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