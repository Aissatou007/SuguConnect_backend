# 📊 Initialisation des Données - SuguConnect

## 🎯 Objectif

Initialiser automatiquement la base de données avec des données de test au démarrage de l'application pour faciliter le développement et les tests.

---

## 🔧 Modifications Apportées

### 1. **ConsommateurService.java** - Amélioration de la gestion des produits vides

**Problème:**
La méthode `voirTousLesProduitsDisponibles()` lançait une exception `EntityNotFoundException` lorsqu'aucun produit n'était disponible.

**Solution:**
Retourner une liste vide au lieu de lancer une exception (meilleure pratique REST).

```java
public List<Produit> voirTousLesProduitsDisponibles() {
    // Retourne tous les produits avec stock > 0
    // Si aucun produit n'est disponible, retourne une liste vide
    return produitRepository.findAllByStockDisponibleGreaterThan(0);
}
```

**Avantages:**
- ✅ Pas d'erreur 500 si aucun produit
- ✅ Retourne 200 OK avec tableau vide `[]`
- ✅ Conforme aux bonnes pratiques REST
- ✅ Meilleure expérience utilisateur

---

### 2. **DataInitializer.java** - Ajout de données de test

**Fonctionnalités:**
Le système initialise automatiquement au démarrage:

#### 👤 **Admin par défaut**
- Téléphone: `70000000`
- Mot de passe: `admin123`
- Email: `admin@suguconnect.com`

#### 📂 **5 Catégories**
1. Fruits
2. Légumes
3. Céréales
4. Produits Laitiers
5. Viandes

#### 👨‍🌾 **2 Producteurs validés**

**Producteur 1:**
- Nom: Traoré Mamadou
- Téléphone: `76543210`
- Mot de passe: `producteur123`
- Localisation: Bamako
- Statut: ACCEPTE (validé par défaut)

**Producteur 2:**
- Nom: Coulibaly Fatou
- Téléphone: `77654321`
- Mot de passe: `producteur123`
- Localisation: Sikasso
- Statut: ACCEPTE (validé par défaut)

#### 🛒 **6 Produits**

**Produits du Producteur 1 (Fruits):**
1. Mangues Bio - 2500 FCFA/kg - Stock: 100 kg
2. Bananes Plantain - 1500 FCFA/kg - Stock: 150 kg
3. Oranges Douces - 1800 FCFA/kg - Stock: 80 kg

**Produits du Producteur 2 (Légumes):**
4. Tomates Fraîches - 1200 FCFA/kg - Stock: 120 kg
5. Oignons Locaux - 800 FCFA/kg - Stock: 200 kg
6. Carottes Bio - 1000 FCFA/kg - Stock: 90 kg

---

### 3. **CategorieRepository.java** - Ajout de méthode

Ajout de la méthode `findByLibelle()` pour rechercher une catégorie par son nom.

```java
Optional<Categorie> findByLibelle(String libelle);
```

---

## 🚀 Utilisation

### Démarrage de l'application

Lors du démarrage de l'application, vous verrez ces messages dans la console:

```
👤 Admin par défaut créé avec succès!
📂 5 catégories créées!
👥 2 producteurs créés et validés!
🛒 6 produits créés avec succès!
✅ Initialisation des données de test terminée!
```

### Tester l'API immédiatement

#### 1. **Voir tous les produits (Public - Sans authentification)**
```http
GET http://localhost:8080/suguconnect/consommateur/produits
```

**Réponse attendue:** Liste de 6 produits

```json
[
  {
    "id": 1,
    "nom": "Mangues Bio",
    "description": "Mangues fraîches et biologiques",
    "prix": 2500.0,
    "stockDisponible": 100,
    "unite": "KG",
    "categorie": {"id": 1, "libelle": "Fruits"}
  },
  // ... 5 autres produits
]
```

#### 2. **Connexion Admin**
```http
POST http://localhost:8080/suguconnect/auth/login/admin
Content-Type: application/json

{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

#### 3. **Connexion Producteur**
```http
POST http://localhost:8080/suguconnect/auth/login/producteur
Content-Type: application/json

{
  "telephone": "76543210",
  "motDePasse": "producteur123"
}
```

---

## 📋 Comportement de l'Initialisation

### Conditions d'exécution

L'initialisation s'exécute **uniquement** si les données n'existent pas déjà:

- ✅ **Admin**: Créé si aucun admin avec le téléphone `70000000`
- ✅ **Catégories**: Créées si `categorieRepository.count() == 0`
- ✅ **Producteurs**: Créés si `producteurRepository.count() == 0`
- ✅ **Produits**: Créés si `produitRepository.count() == 0`

### Sécurité

- ✅ Tous les mots de passe sont **encodés avec BCrypt**
- ✅ Les producteurs sont automatiquement **validés** (StatutProducteur.ACCEPTE)
- ✅ Tous les comptes sont **actifs** par défaut

---

## 🔄 Réinitialiser les Données

Pour réinitialiser complètement les données de test:

### Option 1: Supprimer la base de données
```sql
DROP DATABASE suguConnectDB;
```
Puis redémarrer l'application (la base sera recréée automatiquement).

### Option 2: Vider les tables
```sql
USE suguConnectDB;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE produit;
TRUNCATE TABLE producteur;
TRUNCATE TABLE categorie;
TRUNCATE TABLE admin;
SET FOREIGN_KEY_CHECKS = 1;
```
Puis redémarrer l'application.

---

## 🎯 Avantages

### Pour le Développement
- ✅ **Démarrage rapide**: Données prêtes immédiatement
- ✅ **Tests facilités**: Pas besoin de créer manuellement les données
- ✅ **Cohérence**: Tous les développeurs ont les mêmes données

### Pour la Démonstration
- ✅ **Produits visibles**: L'API retourne des données réelles
- ✅ **Producteurs validés**: Prêts à se connecter
- ✅ **Catégories diversifiées**: Couvre différents cas d'usage

### Pour les Tests
- ✅ **Environnement préparé**: Pas d'erreur "Aucun produit disponible"
- ✅ **Données réalistes**: Prix, stocks, descriptions cohérentes
- ✅ **Relations complètes**: Produits → Producteurs → Catégories

---

## 🔧 Personnalisation

### Modifier les données par défaut de l'admin

Dans `application.properties`:
```properties
admin.nom=MonNom
admin.prenom=MonPrenom
admin.email=monemail@example.com
admin.telephone=12345678
admin.password=monmotdepasse
```

### Ajouter plus de produits

Modifiez la méthode `initProduits()` dans `DataInitializer.java`:

```java
Produit nouveauProduit = new Produit();
nouveauProduit.setNom("Mon Produit");
nouveauProduit.setDescription("Description");
nouveauProduit.setPrix(3000.0);
nouveauProduit.setStockDisponible(50);
nouveauProduit.setUnite(Unite.KG);
nouveauProduit.setCategorie(categorie);
nouveauProduit.setProducteur(producteur);
produitRepository.save(nouveauProduit);
```

### Désactiver l'initialisation automatique

Commentez l'annotation `@Bean` dans `DataInitializer.java`:

```java
// @Bean
@Transactional
public CommandLineRunner initData() {
    // ...
}
```

---

## 📞 Endpoints Disponibles Immédiatement

| Endpoint | Méthode | Authentification | Description |
|----------|---------|------------------|-------------|
| `/consommateur/produits` | GET | ❌ Non | Voir les 6 produits |
| `/categorie` | GET | ❌ Non | Voir les 5 catégories |
| `/producteur/producteurs` | GET | ❌ Non | Voir les 2 producteurs |
| `/auth/login/admin` | POST | ❌ Non | Se connecter admin |
| `/auth/login/producteur` | POST | ❌ Non | Se connecter producteur |
| `/admin/produits` | GET | ✅ ADMIN | Voir tous les produits |
| `/producteur/1/produit` | GET | ✅ PRODUCTEUR | Voir mes produits |

---

## 🎉 Résultat

Après le démarrage:
- ✅ **1 Admin** prêt à gérer la plateforme
- ✅ **2 Producteurs** validés et prêts à vendre
- ✅ **5 Catégories** pour organiser les produits
- ✅ **6 Produits** disponibles à l'achat
- ✅ **API fonctionnelle** avec données réelles

**Plus d'erreur "Aucun produit n'est disponible pour le moment"!** 🎊
