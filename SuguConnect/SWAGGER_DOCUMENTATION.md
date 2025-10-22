# Documentation API SuguConnect - Swagger

## 🎯 Vue d'ensemble

Cette documentation complète décrit toutes les APIs REST disponibles dans la plateforme SuguConnect, une solution de mise en relation entre producteurs et consommateurs.

## 🚀 Accès à la Documentation

### URL de la documentation Swagger UI

Une fois l'application démarrée, accédez à la documentation interactive à l'adresse :

```
http://localhost:8080/suguconnect/swagger-ui.html
```

### URL de la spécification OpenAPI

Pour obtenir la spécification JSON de l'API :

```
http://localhost:8080/suguconnect/v3/api-docs
```

## 📚 Structure de l'API

L'API SuguConnect est organisée en 5 modules principaux :

### 1. **Administrateur** 👤
Gestion des administrateurs et supervision globale de la plateforme.

**Endpoints disponibles :**
- `POST /admin/inscription` - Créer un nouvel administrateur
- `GET /admin/admins` - Récupérer tous les administrateurs
- `GET /admin/{id}` - Récupérer un administrateur spécifique
- `PUT /admin/{id}` - Modifier un administrateur
- `DELETE /admin/{id}` - Supprimer un administrateur
- `POST /admin/producteurs/ajouter` - Ajouter un producteur
- `PUT /admin/producteurs/{id}/statut` - Valider ou refuser un producteur
- `GET /admin/producteurs` - Lister tous les producteurs
- `GET /admin/consommateurs` - Lister tous les consommateurs
- `GET /admin/produits` - Lister tous les produits
- `GET /admin/commandes` - Lister toutes les commandes
- `GET /admin/paiements` - Lister tous les paiements

### 2. **Producteur** 🌾
Gestion des producteurs et de leurs produits.

**Endpoints disponibles :**
- `POST /producteur/inscription` - Inscription d'un producteur
- `POST /producteur/connexion` - Connexion d'un producteur
- `GET /producteur/producteurs` - Lister tous les producteurs
- `GET /producteur/{id}` - Récupérer un producteur spécifique
- `PUT /producteur/{id}` - Modifier un producteur
- `DELETE /producteur/{id}` - Supprimer un producteur
- `POST /producteur/{producteurId}/produit` - Ajouter un produit
- `GET /producteur/{producteurId}/produit` - Lister les produits d'un producteur
- `PUT /producteur/{producteurId}/produit/{produitId}` - Modifier un produit
- `DELETE /producteur/{producteurId}/produit/{produitId}` - Supprimer un produit

### 3. **Consommateur** 🛒
Gestion des consommateurs, paniers et commandes.

**Endpoints disponibles :**
- `POST /consommateur/inscription` - Inscription d'un consommateur
- `GET /consommateur/consommateurs` - Lister tous les consommateurs
- `GET /consommateur/{id}` - Récupérer un consommateur spécifique
- `PUT /consommateur/{id}` - Modifier un consommateur
- `DELETE /consommateur/{id}` - Supprimer un consommateur
- `GET /consommateur/produits` - Voir les produits disponibles
- `POST /consommateur/{idConsommateur}/panier/ajouter/{idProduit}` - Ajouter au panier
- `DELETE /consommateur/{idConsommateur}/panier/retirer/{idProduit}` - Retirer du panier
- `POST /consommateur/{idConsommateur}/commande` - Passer une commande

### 4. **Catégorie** 📦
Gestion des catégories de produits.

**Endpoints disponibles :**
- `POST /categorie` - Créer une catégorie (Admin uniquement)
- `GET /categorie` - Lister toutes les catégories
- `GET /categorie/{id}` - Récupérer une catégorie spécifique
- `PUT /categorie/{id}` - Modifier une catégorie (Admin uniquement)
- `DELETE /categorie/{id}` - Supprimer une catégorie (Admin uniquement)
- `GET /categorie/{id}/produits` - Récupérer les produits d'une catégorie

### 5. **Paiement** 💳
Gestion des paiements et transactions.

**Endpoints disponibles :**
- `POST /api/paiements` - Créer un paiement
- `GET /api/paiements` - Lister tous les paiements
- `GET /api/paiements/{id}` - Récupérer un paiement spécifique
- `GET /api/paiements/consommateur/{consommateurId}` - Paiements par consommateur
- `PUT /api/paiements/{id}/statut` - Mettre à jour le statut d'un paiement
- `DELETE /api/paiements/{id}` - Supprimer un paiement

## 🔐 Authentification

### Compte Administrateur par Défaut

L'application crée automatiquement un compte administrateur au démarrage :

- **Téléphone** : `70000000`
- **Mot de passe** : `admin123`
- **Email** : `admin@suguconnect.com`

### Statuts des Producteurs

Les producteurs ont 3 statuts possibles :
- `EN_ATTENTE` : Compte en attente de validation
- `ACCEPTE` : Compte validé, peut ajouter des produits
- `REFUSE` : Compte refusé

## 📊 Codes de Réponse HTTP

| Code | Signification | Description |
|------|--------------|-------------|
| 200 | OK | Requête réussie |
| 201 | Created | Ressource créée avec succès |
| 400 | Bad Request | Données invalides ou manquantes |
| 401 | Unauthorized | Authentification requise |
| 403 | Forbidden | Accès refusé (privilèges insuffisants) |
| 404 | Not Found | Ressource non trouvée |
| 500 | Internal Server Error | Erreur serveur |

## 🔄 Énumérations

### StatutProducteur
- `EN_ATTENTE` : En attente de validation
- `ACCEPTE` : Producteur validé
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
- `ORANGE_MONEY` : Paiement via Orange Money
- `MOOV_MONEY` : Paiement via Moov Money

### ModeLivraison
- `VIA_PLATFORM` : Livraison via la plateforme
- `VIA_CONSOMMATEUR` : Récupération par le consommateur

### Unite
- `KILOGRAMME`, `GRAMME`, `TONNE`
- `LITRE`, `MILLILITRE`
- `SAC`, `BOTTE`, `PIECE`

### Role
- `ADMIN` : Administrateur de la plateforme
- `PRODUCTEUR` : Producteur de produits
- `CONSOMMATEUR` : Consommateur/Acheteur

## 🛠️ Utilisation de Swagger UI

### Tester les Endpoints

1. **Accédez à Swagger UI** : `http://localhost:8080/suguconnect/swagger-ui.html`

2. **Sélectionnez une catégorie** (Administrateur, Producteur, etc.)

3. **Choisissez un endpoint** à tester

4. **Cliquez sur "Try it out"**

5. **Remplissez les paramètres** requis

6. **Cliquez sur "Execute"**

7. **Consultez la réponse** dans la section "Responses"

### Fonctionnalités Swagger UI

- ✅ **Tri des opérations** par méthode HTTP
- ✅ **Filtre** pour rechercher rapidement un endpoint
- ✅ **Modèles** de données détaillés
- ✅ **Exemples** de requêtes et réponses
- ✅ **Essai direct** depuis l'interface
- ✅ **Documentation complète** en français

## 📝 Exemples de Requêtes

### Exemple 1 : Inscription d'un Producteur

```json
POST /suguconnect/producteur/inscription
Content-Type: application/json

{
  "nom": "Kouassi",
  "prenom": "Jean",
  "telephone": "0123456789",
  "email": "jean.kouassi@example.com",
  "localisation": "Abidjan",
  "latitude": 5.3600,
  "longitude": -4.0083,
  "motDePasse": "monMotDePasse123",
  "description": "Producteur de fruits et légumes bio"
}
```

### Exemple 2 : Ajouter un Produit au Panier

```json
POST /suguconnect/consommateur/12/panier/ajouter/5?quantite=3
```

### Exemple 3 : Passer une Commande

```json
POST /suguconnect/consommateur/12/commande?modePaiement=ORANGE_MONEY
```

### Exemple 4 : Changer le Statut d'un Producteur

```json
PUT /suguconnect/admin/producteurs/8/statut?statut=ACCEPTE
```

## 🔧 Configuration Technique

### Dépendances Maven

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Configuration application.properties

```properties
# Swagger/OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
```

## 📞 Support et Contact

Pour toute question ou problème :
- **Email** : contact@suguconnect.com
- **Documentation** : [https://suguconnect.com/docs](https://suguconnect.com/docs)

## 🎓 Ressources Supplémentaires

- [Swagger Documentation](https://swagger.io/docs/)
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

**Version de l'API** : 1.0.0  
**Dernière mise à jour** : 2025-10-21  
**Plateforme** : SuguConnect - Mise en relation Producteurs & Consommateurs
