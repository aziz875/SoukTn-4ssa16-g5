# SoukTn — 4SSA16 — Groupe 5

> **Marketplace e-commerce multi-vendeurs** tunisienne, évolutive et résiliente, basée sur une architecture en microservices.

---

## 📋 Problématique

Le retrait de Jumia du marché tunisien (fin 2024) a mis en lumière l'absence d'une alternative nationale robuste. Une plateforme monolithique souffrirait de :
- **Couplage fort** : la défaillance d'un module (ex. paiement) peut paralyser toute la plateforme.
- **Scalabilité limitée** : impossible d'allouer des ressources ciblées aux services les plus sollicités.
- **Complexité multi-vendeurs** : flux hétérogènes (stocks, livraisons, paiements, litiges) difficiles à faire évoluer dans un même code.
- **Rigidité des données** : un modèle unique doit stocker à la fois des données financières strictes et des fiches produits aux attributs variables.

**SoukTn** répond à ces problèmes avec 7 microservices autonomes, chacun propriétaire de sa base de données.

---

## 👥 Acteurs

| Acteur | Rôle |
|--------|------|
| **Visiteur** | Consulte le catalogue, recherche des produits, remplit un panier, s'inscrit |
| **Client** | Passe et paie ses commandes, suit ses livraisons, consulte ses factures, laisse des avis, ouvre des litiges |
| **Vendeur** | Gère sa boutique, ses produits et ses stocks, consulte ses commandes reçues, répond aux litiges |
| **Livreur** | Consulte ses livraisons assignées et met à jour leur statut |
| **Administrateur** | Gère les utilisateurs, valide les vendeurs, gère les catégories, assigne les livraisons, traite les litiges |

Acteurs secondaires (systèmes) : **Keycloak**, **passerelle de paiement** (Konnect / Paymee sandbox), **serveur e-mail (SMTP)**.

---

## ✨ Principales fonctionnalités

- 🔐 **Comptes & autorisations** : authentification déléguée à Keycloak (OpenID Connect, JWT), rôles CLIENT / VENDOR / ADMIN / DELIVERY.
- 🛍️ **Catalogue multi-vendeurs** : produits, catégories, stocks, recherche plein texte.
- 🛒 **Panier & commandes** : panier, commande, suivi (En attente → Confirmée → Expédiée → Livrée ; annulation possible uniquement avant paiement).
- 💳 **Paiement & facturation** : Konnect / Paymee en sandbox + simulation (mock), factures PDF, remboursements.
- 🚚 **Livraison** : assignation des livreurs, suivi des étapes.
- ⚖️ **Litiges & retours** : cycle de vie géré par Spring StateMachine.
- 🔔 **Avis & notifications** : avis sur produits achetés et livrés, notifications e-mail et dans l'application.

---

## 🏗️ Architecture globale

![Diagramme d'architecture globale SoukTn](Documentation/Diagrams/Architecture_Globale.jpg)

### Composants d'infrastructure

| Composant | Technologie | Port | Rôle |
|-----------|-------------|------|------|
| **API Gateway** | Spring Cloud Gateway | 8080 | Point d'entrée unique, validation du JWT, routage `lb://`, CORS |
| **Discovery Server** | Spring Cloud Netflix Eureka | 8761 | Enregistrement et localisation des microservices |
| **Config Server** | Spring Cloud Config | 8888 | Configuration centralisée (dossier `config-repo`), mode Discovery First |
| **Keycloak** | Keycloak (realm `marketplace`) | 8180 | Identités, connexion OIDC, émission des JWT, rôles |
| **RabbitMQ** | RabbitMQ (exchange `marketplace.events`, type topic) | 5672 / 15672 | Communication asynchrone par événements |

### Communications
- **Synchrone** (REST / OpenFeign + Resilience4j) : uniquement quand la réponse est nécessaire pour continuer — Order → User, Order → Catalog, Dispute → Order.
- **Asynchrone** (RabbitMQ) : 7 événements — `USER_REGISTERED`, `ORDER_CREATED`, `ORDER_CANCELLED`, `PAYMENT_COMPLETED`, `PAYMENT_FAILED`, `DELIVERY_UPDATED`, `DISPUTE_RESOLVED`. Files durables, consommateurs idempotents (`eventId`), DLQ après 3 échecs.

---

## 🧩 Microservices

| # | Service | Port | Responsable | Base de données | Responsabilités | Infra |
|---|---------|------|-------------|-----------------|-----------------|-------|
| 0 | **User & Vendor Service** (commun) | 8081 | *Toute l'équipe* | MySQL `user_db` | Profils, adresses, boutiques, validation des vendeurs (comptes créés dans Keycloak via l'API Admin) | Keycloak (équipe) |
| 1 | **Product & Catalog Service** | 8082 | Hendez Badii | PostgreSQL `catalog_db` | Catalogue, catégories, stocks, recherche (JSONB, plein texte) | Discovery Server |
| 2 | **Order Service** | 8083 | Ben Azzoun Myriam | MySQL `order_db` | Panier, commandes, suivi | API Gateway |
| 3 | **Payment & Billing Service** | 8084 | Nouaigui Marouen | MySQL `payment_db` | Paiement sandbox (Konnect / Paymee) + mock, factures, remboursements | RabbitMQ + Config Server |
| 4 | **Delivery & Logistics Service** | 8085 | Jdidi Aziz | MySQL `delivery_db` | Assignation des livreurs, suivi des expéditions | Docker Compose |
| 5 | **Dispute & Support Service** | 8086 | Zerzeri Yasmine | MySQL `dispute_db` | Litiges, retours, Spring StateMachine | Support architecture |
| 6 | **Review & Notification Service** | 8087 | Ben Maghzouz Ahmed | MySQL `notification_db` | Avis produits, notifications e-mail et in-app | Base Frontend Angular |

Keycloak utilise sa propre base PostgreSQL `keycloak_db`. Aucune base n'est partagée entre services.

---

## 🛠️ Stack technique

| Couche | Technologies |
|--------|-------------|
| **Backend** | Java 21, Spring Boot 4.2.0, Spring Cloud (Gateway, Eureka, Config, OpenFeign), Spring Data JPA, Spring Security (Resource Server), Spring StateMachine |
| **Frontend** | Angular 21, Bootstrap, Angular Material, keycloak-angular |
| **Bases de données** | MySQL (6 services), PostgreSQL (catalogue, Keycloak) |
| **Messaging** | RabbitMQ |
| **Sécurité** | Keycloak, OAuth2 / OpenID Connect, JWT |
| **Résilience** | Resilience4j (timeout, retry, circuit breaker) |
| **Conteneurisation** | Docker, Docker Compose |
| **API Docs** | Swagger / OpenAPI (springdoc) |

---

## 📁 Structure du dépôt

```
SoukTn-4ssa16-g5/
├── Backend/
│   ├── api-gateway/
│   ├── discovery-server/
│   ├── config-server/
│   ├── config-repo/          # fichiers YAML lus par le Config Server
│   ├── user-service/
│   ├── catalog-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── delivery-service/
│   ├── dispute-service/
│   └── notification-service/
├── Frontend/
│   └── ecommerce-web-app/
├── Documentation/
│   ├── Presentation/
│   └── Diagrams/
├── docker-compose.yml
└── README.md
```

### Conventions
- Versions : Java 21, Spring Boot 4.2.0 (Spring Cloud 2026.0.0), Maven, configuration en YAML (`application.yml`)
- `groupId` : `tn.souktn` — package : `tn.souktn.<service>`
- `spring.application.name` : `user`, `catalog`, `order`, `payment`, `delivery`, `dispute`, `notification` (correspond aux routes `lb://USER`, `lb://CATALOG`… de la Gateway)
- Ordre de démarrage : Discovery Server → Config Server → Keycloak / RabbitMQ / bases → microservices → Gateway

## API Gateway

The Spring Cloud Gateway project is in `Backend/api-gateway`. It is scoped to
the Notification Service only and uses Eureka service discovery to route:

| Gateway path | Eureka service |
|---|---|
| `/api/notifications/**` | `NOTIFICATION` |
| `/api/reviews/**` | `NOTIFICATION` |

Its Swagger UI documents the Notification API at
`http://localhost:8080/swagger-ui.html`. Gateway and load-balancer debug logs
are enabled to make route selection observable. The gateway uses Spring
Cloud's default load-balancer strategy. No user-service or Keycloak
configuration is included in this Gateway.

### Start the Gateway (Windows PowerShell)

Start Eureka, Config Server, and Notification Service first in separate
terminals. Then, from the repository root, run:

```powershell
Set-Location .\Backend
.\config-server\mvnw.cmd -pl api-gateway spring-boot:run
```

The Gateway listens on port `8080`. Its health endpoint is
`http://localhost:8080/actuator/health`; the Notification Swagger portal is
`http://localhost:8080/swagger-ui.html`. For notification routes and docs to
resolve, Eureka (`8761`) and Notification Service (`8087`) should be running
and registered.

If port `8080` is already in use, start it on `8090` instead:

```powershell
.\config-server\mvnw.cmd -pl api-gateway spring-boot:run "-Dspring-boot.run.arguments=--server.port=8090"
```

---

## ➕ Ajouter un service au backend

Toutes les versions sont imposées par le POM parent `Backend/pom.xml` (`tn.souktn:souktn-backend`). Un service ne déclare **aucune** version.

1. `git checkout main && git pull`, puis `git checkout -b feature/<service>`.
2. Générer le projet dans `Backend/` (IntelliJ ou start.spring.io) : Maven, Java 21, Spring Boot 4.2.0, configuration YAML, group `tn.souktn`, package `tn.souktn.<service>`, sans créer de dépôt Git.
3. Dans `Backend/<service>/pom.xml`, remplacer le bloc `<parent>` par le bloc ci-dessous, puis supprimer `<groupId>`, `<version>`, `<properties>` et `<dependencyManagement>`. Les dépendances restent **sans version**.

   ```xml
   <parent>
       <groupId>tn.souktn</groupId>
       <artifactId>souktn-backend</artifactId>
       <version>1.0.0-SNAPSHOT</version>
       <relativePath>../pom.xml</relativePath>
   </parent>
   ```

4. Ajouter `<module>nom-du-dossier</module>` dans la section `<modules>` de `Backend/pom.xml`.
5. Vérifier depuis `Backend/` : `mvn clean package -DskipTests` doit afficher `SUCCESS` pour tous les modules.

Exemple de référence : `Backend/config-server/pom.xml`.

### Checklist du relecteur (PR d'un nouveau service)
- [ ] Le `<parent>` est `souktn-backend`
- [ ] Aucune version de Spring Boot, Spring Cloud ou Java dans le `pom.xml` du service
- [ ] Le module est ajouté dans `Backend/pom.xml`
- [ ] `mvn clean package -DskipTests` réussit depuis `Backend/`
- [ ] `spring.application.name` respecte la convention

---

## 🌿 Gitflow

| Branche | Rôle |
|---------|------|
| `main` | Version stable, prête à la démo |
| `develop` | Branche d'intégration |
| `feature/<service>` | Développement par microservice (ex. `feature/catalog-service`) |
| `docs/<sujet>` | Documentation |

Chaque branche est fusionnée par Pull Request relue par un autre membre. Commits au format Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`).

---

## 🗓️ Planning (sprints)

| Sprint | Thème | Contenu |
|--------|-------|---------|
| **Sprint 1** | Conception | Besoins, architecture, diagrammes UML, contrats d'API, squelettes des microservices |
| **Sprint 2** | Socle & CRUD | API REST de base, Discovery Server, Config Server, API Gateway, Docker Compose des bases |
| **Sprint 3** | Communications & logique avancée | OpenFeign, RabbitMQ, Spring StateMachine, paiement sandbox, sécurité Keycloak, intégration Angular |
| **Sprint 4** | Tests & livraison | Tests d'intégration, conteneurisation complète, recette |

---

## ⚠️ Gestion des risques

| Risque | Impact | Mitigation |
|--------|--------|-----------|
| Incohérence des API inter-services | 🔴 Élevé | Contrats d'API stricts via Swagger / OpenAPI |
| Panne d'un microservice | 🔴 Élevé | Resilience4j (timeout, retry, circuit breaker) sur les appels OpenFeign |
| Perte ou doublon d'événements | 🟡 Moyen | Files durables, consommateurs idempotents (`eventId`), DLQ |
| Sandbox de paiement indisponible | 🟡 Moyen | Simulation interne (mock) derrière la même interface |
| Retard d'intégration du frontend | 🟡 Moyen | Mocks et Swagger pour une consommation anticipée |

---

## 📂 Documentation

- `Documentation/` : documentation technique du projet
- `Documentation/Diagrams/` : diagramme d'architecture globale et diagrammes UML
- `Documentation/Presentation/` : supports de présentation
