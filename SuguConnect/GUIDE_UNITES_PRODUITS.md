# 📦 Guide de Création de Produits - Unités de Mesure

## ✅ Valeurs d'Unités Disponibles

L'enum `Unite` définit les unités de mesure suivantes pour les produits:

### 🏋️ Poids
- **`KILOGRAMME`** - Pour les produits vendus au kilogramme (kg)
  - Exemples: Riz, Maïs, Tomates, Oignons
  
- **`GRAMME`** - Pour les petites quantités (g)
  - Exemples: Épices, Herbes aromatiques
  
- **`TONNE`** - Pour les grandes quantités (t)
  - Exemples: Céréales en gros, Engrais

### 💧 Volume
- **`LITRE`** - Pour les liquides (L)
  - Exemples: Lait, Huile, Jus de fruits
  
- **`MILLILITRE`** - Pour les petits volumes (mL)
  - Exemples: Essences, Extraits

### 📦 Conditionnement
- **`SAC`** - Pour les produits en sac
  - Exemples: Sac de riz (25kg), Sac de ciment
  
- **`BOTTE`** - Pour les produits en botte
  - Exemples: Carottes en botte, Persil en botte
  
- **`PIECE`** - Pour les produits à l'unité
  - Exemples: Ananas, Pastèque, Chou

---

## 📝 Exemples de Création de Produits

### Exemple 1: Produit au Kilogramme
```json
{
  "nom": "Tomates Fraîches",
  "description": "Tomates rouges et mûres de notre ferme",
  "prixUnitaire": 1200.0,
  "unite": "KILOGRAMME",
  "stockDisponible": 150,
  "categorieId": 2
}
```

### Exemple 2: Produit à la Pièce
```json
{
  "nom": "Ananas Victoria",
  "description": "Ananas juteux et sucré",
  "prixUnitaire": 2000.0,
  "unite": "PIECE",
  "stockDisponible": 50,
  "categorieId": 1
}
```

### Exemple 3: Produit au Litre
```json
{
  "nom": "Lait Frais",
  "description": "Lait de vache frais du jour",
  "prixUnitaire": 800.0,
  "unite": "LITRE",
  "stockDisponible": 100,
  "categorieId": 4
}
```

### Exemple 4: Produit en Sac
```json
{
  "nom": "Riz Local",
  "description": "Riz blanc de qualité - Sac de 25kg",
  "prixUnitaire": 15000.0,
  "unite": "SAC",
  "stockDisponible": 30,
  "categorieId": 3
}
```

### Exemple 5: Produit en Botte
```json
{
  "nom": "Carottes Bio",
  "description": "Carottes biologiques fraîches - Botte de 1kg",
  "prixUnitaire": 1500.0,
  "unite": "BOTTE",
  "stockDisponible": 80,
  "categorieId": 2
}
```

---

## 🔧 Utilisation dans l'API

### Endpoint de Création de Produit
```http
POST /producteur/produits/ajouter
Authorization: Bearer <token-producteur>
Content-Type: application/json

{
  "nom": "Nom du produit",
  "description": "Description",
  "prixUnitaire": 2500.0,
  "unite": "KILOGRAMME",  ⬅️ UNE DES VALEURS CI-DESSUS
  "stockDisponible": 100,
  "categorieId": 1
}
```

---

## ⚠️ Erreurs Communes

### ❌ Erreur 1: Unité invalide
```json
{
  "unite": "KG"  // INCORRECT
}
```
**Solution:** Utiliser la valeur exacte de l'enum:
```json
{
  "unite": "KILOGRAMME"  // ✅ CORRECT
}
```

### ❌ Erreur 2: Minuscules
```json
{
  "unite": "kilogramme"  // INCORRECT
}
```
**Solution:** Les valeurs doivent être en MAJUSCULES:
```json
{
  "unite": "KILOGRAMME"  // ✅ CORRECT
}
```

### ❌ Erreur 3: Valeur numérique
```json
{
  "unite": 1  // INCORRECT
}
```
**Solution:** Utiliser la chaîne de caractères:
```json
{
  "unite": "KILOGRAMME"  // ✅ CORRECT
}
```

---

## 📊 Recommandations par Type de Produit

| Type de Produit | Unité Recommandée | Exemple |
|----------------|-------------------|---------|
| Légumes frais | `KILOGRAMME` ou `BOTTE` | Tomates, Carottes |
| Fruits | `KILOGRAMME` ou `PIECE` | Mangues (kg), Ananas (pièce) |
| Céréales | `KILOGRAMME` ou `SAC` | Riz (sac de 25kg) |
| Liquides | `LITRE` | Lait, Huile |
| Épices | `GRAMME` | Poivre, Cannelle |
| Gros volumes | `TONNE` | Engrais, Céréales en gros |

---

## 🎯 Dans Swagger UI

Quand vous utilisez Swagger UI:

1. **Ouvrez l'endpoint** `POST /producteur/produits/ajouter`
2. **Cliquez sur "Try it out"**
3. **Dans le champ `unite`**, vous devriez voir un **dropdown** avec toutes les valeurs
4. **Si le dropdown n'apparaît pas**, tapez directement la valeur en MAJUSCULES

### Valeurs à copier-coller:
```
KILOGRAMME
GRAMME
TONNE
LITRE
MILLILITRE
SAC
BOTTE
PIECE
```

---

## 📱 Test Rapide

Pour vérifier que les unités fonctionnent, créez ce produit de test:

```http
POST /producteur/produits/ajouter
Authorization: Bearer <votre-token-producteur>
Content-Type: application/json

{
  "nom": "Produit Test",
  "description": "Test des unités",
  "prixUnitaire": 1000.0,
  "unite": "KILOGRAMME",
  "stockDisponible": 10,
  "categorieId": 1
}
```

**Si ça fonctionne**, toutes les autres unités fonctionneront aussi!

---

## 💡 Notes Importantes

1. **Les valeurs sont CASE-SENSITIVE** (sensibles à la casse)
2. **Utilisez exactement les valeurs listées** (pas d'abréviations)
3. **Dans Swagger**, si le dropdown n'apparaît pas, redémarrez l'application
4. **Les valeurs sont en français** sauf pour certaines unités scientifiques

---

**Dernière mise à jour:** 2025-10-23  
**Fichier source:** `src/main/java/odk/SuguConnect/Enums/Unite.java`
