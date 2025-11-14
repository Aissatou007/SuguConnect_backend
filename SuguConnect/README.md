# SuguConnect - Backend

## Description du Projet

SuguConnect est une plateforme de mise en relation entre producteurs et consommateurs, facilitant les transactions directes de produits agricoles et locaux.

## Technologies Utilisées

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring Security**
- **MySQL**
- **JWT (JSON Web Tokens)**
- **Lombok**
- **Maven**

## Prérequis

- Java 17 ou supérieur
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur

## Configuration

### Base de données

1. Créer une base de données MySQL nommée `suguConnectDB`
2. Configurer les paramètres de connexion dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/suguConnectDB
spring.datasource.username=root
spring.datasource.password=VotreMotDePasse
```

### Administrateur par défaut

Un compte administrateur est créé automatiquement au démarrage avec les identifiants suivants :
- **Téléphone** : 70000000
- **Mot de passe** : admin123

Vous pouvez modifier ces valeurs dans le fichier `application.properties`.

## Installation et Exécution

### 1. Cloner le projet
```bash
git clone [url-du-repo]
cd SuguConnect
```

### 2. Installer les dépendances
```bash
mvn clean install
```

### 3. Lancer l'application
```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : `http://localhost:8080/suguconnect`

## Architecture du Projet

### Structure des dossiers

```
src/main/java/odk/SuguConnect/
├── Config/              # Configuration (Security, DataInitializer)
├── Controller/          # Contrôleurs REST
├── DTO/                 # Objets de transfert de données
│   ├── Request/        # DTOs pour les requêtes
│   ├── Responses/      # DTOs pour les réponses
│   └── SimpleDTO/      # DTOs simples
├── Entity/              # Entités JPA
├── Enums/               # Énumérations
├── Interface/           # Interfaces
├── Mapper/              # Mappers Entity <-> DTO
├── Repository/          # Repositories JPA
├── Security/            # Services de sécurité (JWT)
└── Service/             # Logique métier
```

### Entités principales

- **Utilisateur** (classe abstraite) : Admin, Producteur, Consommateur
- **Produit** : Articles proposés par les producteurs
- **Catégorie** : Classification des produits
- **Commande** : Commandes passées par les consommateurs
- **Paiement** : Transactions financières
- **Panier** : Panier d'achat des consommateurs
- **Livraison** : Suivi des livraisons
- **Notification** : Notifications système
- **Conversation** : Discussions entre consommateurs et producteurs
- **Message** : Messages échangés dans les conversations

## API Endpoints

### Administrateur (`/admin`)
- `POST /admin/inscription` - Créer un admin
- `GET /admin/admins` - Lister tous les admins
- `GET /admin/{id}` - Récupérer un admin
- `PUT /admin/{id}` - Modifier un admin
- `DELETE /admin/{id}` - Supprimer un admin
- `POST /admin/producteurs/ajouter` - Ajouter un producteur
- `PUT /admin/producteurs/{id}/statut` - Changer le statut d'un producteur
- `GET /admin/producteurs` - Voir tous les producteurs
- `GET /admin/consommateurs` - Voir tous les consommateurs
- `GET /admin/produits` - Voir tous les produits
- `GET /admin/commandes` - Voir toutes les commandes
- `GET /admin/paiements` - Voir tous les paiements

### Producteur (`/producteur`)
- `POST /producteur/inscription` - Inscription d'un producteur
- `POST /producteur/connexion` - Connexion
- `GET /producteur/producteurs` - Lister tous les producteurs
- `GET /producteur/{id}` - Récupérer un producteur
- `PUT /producteur/{id}` - Modifier un producteur
- `DELETE /producteur/{id}` - Supprimer un producteur
- `POST /producteur/{producteurId}/produit` - Ajouter un produit
- `GET /producteur/{producteurId}/produit` - Lister les produits
- `PUT /producteur/{producteurId}/produit/{produitId}` - Modifier un produit
- `DELETE /producteur/{producteurId}/produit/{produitId}` - Supprimer un produit

### Consommateur (`/consommateur`)
- `POST /consommateur/inscription` - Inscription
- `GET /consommateur/consommateurs` - Lister tous les consommateurs
- `GET /consommateur/{id}` - Récupérer un consommateur
- `PUT /consommateur/{id}` - Modifier un consommateur
- `DELETE /consommateur/{id}` - Supprimer un consommateur
- `GET /consommateur/produits` - Voir les produits disponibles
- `POST /consommateur/{idConsommateur}/panier/ajouter/{idProduit}` - Ajouter au panier
- `DELETE /consommateur/{idConsommateur}/panier/retirer/{idProduit}` - Retirer du panier
- `POST /consommateur/{idConsommateur}/commande` - Passer une commande

### Chat (`/api/chat`)
- `POST /api/chat/conversation` - Créer ou récupérer une conversation
- `GET /api/chat/conversations/consommateur/{consommateurId}` - Récupérer les conversations d'un consommateur
- `GET /api/chat/conversations/producteur/{producteurId}` - Récupérer les conversations d'un producteur
- `POST /api/chat/message/texte` - Envoyer un message texte
- `POST /api/chat/message/fichier` - Envoyer un message avec fichier
- `GET /api/chat/messages/{conversationId}` - Récupérer les messages d'une conversation
- `PUT /api/chat/message/{messageId}/lu` - Marquer un message comme lu
- `POST /api/chat/upload/fichier` - Uploader un fichier pour le chat
- `GET /api/chat/download/{fileName}` - Télécharger un fichier de chat

### Catégorie (`/categorie`)
- `POST /categorie` - Créer une catégorie
- `GET /categorie` - Lister toutes les catégories
- `GET /categorie/{id}` - Récupérer une catégorie
- `PUT /categorie/{id}` - Modifier une catégorie
- `DELETE /categorie/{id}` - Supprimer une catégorie
- `GET /categorie/{id}/produits` - Produits par catégorie

## Énumérations

### StatutProducteur
- `EN_ATTENTE` : En attente de validation
- `ACCEPTE` : Producteur accepté
- `REFUSE` : Producteur refusé

### StatutCommande
- `EN_ATTENTE` : Commande en attente
- `EN_COURS` : Commande en cours de traitement
- `VALIDEE` : Commande validée
- `LIVREE` : Commande livrée
- `DECLINEE` : Commande déclinée

### StatutPaiement
- `INITIE` : Paiement initié
- `EN_ATTENTE` : En attente de confirmation
- `VALIDE` : Paiement validé
- `ECHOUE` : Paiement échoué

### ModePaiement
- `ORANGE_MONEY`
- `MOOV_MONEY`

### ModeLivraison
- `VIA_PLATFORM` : Livraison via la plateforme
- `VIA_CONSOMMATEUR` : Récupération par le consommateur

### Unite
- `KILOGRAMME`, `GRAMME`, `TONNE`
- `LITRE`, `MILLILITRE`
- `SAC`, `BOTTE`, `PIECE`

### Role
- `ADMIN` : Administrateur
- `PRODUCTEUR` : Producteur
- `CONSOMMATEUR` : Consommateur

### TypeMessage
- `TEXTE` : Message texte
- `IMAGE` : Message avec image
- `VOCAL` : Message vocal
- `DOCUMENT` : Message avec document
- Types de notification (COMMANDE_PASSEE, COMMANDE_VALIDEE, etc.)

## Sécurité

- Spring Security configuré avec JWT
- Endpoints publics pour l'inscription et la connexion
- Protection des endpoints d'administration
- Encodage des mots de passe avec BCrypt

## Tests

Pour exécuter les tests :
```bash
mvn test
```

## Contribution

1. Forker le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commiter vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Pousser vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence [TYPE DE LICENCE].

## Contact

Pour toute question ou suggestion, contactez l'équipe de développement.

---

**Note** : Ce projet est en développement actif. Des fonctionnalités supplémentaires seront ajoutées régulièrement.