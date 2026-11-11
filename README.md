# SoukTn — 4SSA16 — Groupe 5

> Plateforme e-commerce tunisienne basée sur une architecture microservices.

## 📁 Structure du dépôt

```
SoukTn-4ssa16-g5/
├── Backend/               # Microservices (Spring Boot / Node.js / ...)
├── Frontend/              # Application cliente (React / Angular / ...)
├── Documentation/
│   ├── Presentation/      # Présentation séance 4 (PowerPoint / PDF)
│   └── Diagrams/          # Diagrammes d'architecture et UML
└── README.md
```

## 🏗️ Architecture

Le projet repose sur une architecture **microservices** avec :

- **API Gateway** — point d'entrée unique pour les clients
- **Service d'authentification** — gestion des utilisateurs et JWT
- **Service produits** — catalogue et gestion des articles
- **Service commandes** — gestion du cycle de vie des commandes
- **Service paiement** — intégration passerelle de paiement
- **Frontend** — interface utilisateur SPA

## 🚀 Démarrage rapide

### Prérequis

- Docker & Docker Compose
- Node.js >= 18
- Java 17+ (pour les services Spring Boot)

### Lancer le projet

```bash
# Cloner le dépôt
git clone <url-du-repo>
cd SoukTn-4ssa16-g5

# Démarrer tous les services
docker compose up --build
```

## 👥 Équipe

| Membre | Rôle |
|--------|------|
|        |      |
|        |      |
|        |      |
|        |      |

## 📄 Documentation

- [Diagramme d'architecture](Documentation/Diagrams/)
- [Présentation Séance 4](Documentation/Presentation/)

## 🔗 Liens utiles

- Tableau de bord : _à compléter_
- API Docs (Swagger) : _à compléter_
