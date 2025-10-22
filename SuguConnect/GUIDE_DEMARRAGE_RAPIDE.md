# 🚀 Guide de Démarrage Rapide - SuguConnect

## ✅ Fonctionnalités Implémentées

Toutes vos demandes ont été intégrées :

1. ✅ **Photo et nom de ferme** pour les producteurs
2. ✅ **Notifications automatiques** à chaque action (commande, validation, livraison, etc.)
3. ✅ **Seul le producteur** peut marquer une commande comme "LIVREE"
4. ✅ **Validation de réception** par le consommateur
5. ✅ **Système d'avis** avec notes et commentaires

---

## 🎯 Démarrage de l'Application

### 1. Démarrer l'application
```bash
cd "c:\Users\PC\Documents\Mes projets\Spring\SuguConnect_backend\SuguConnect"
mvn spring-boot:run
```

### 2. Accéder à Swagger
```
http://localhost:8080/suguconnect/swagger-ui.html
```

---

## 📋 Scénario Complet de Test

### ÉTAPE 1 : Créer un Producteur avec Photo et Nom de Ferme

**Endpoint :** `POST /producteur/inscription`

```json
{
  "nom": "Diallo",
  "prenom": "Amadou",
  "telephone": "77777777",
  "email": "amadou@ferme.com",
  "localisation": "Conakry",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "password123",
  "description": "Producteur de fruits biologiques",
  "nomFerme": "Ferme Bio d'Amadou"
}
```

**Résultat :** Producteur créé (statut EN_ATTENTE)

---

### ÉTAPE 2 : Login Admin et Validation du Producteur

**2.1 Login Admin**
```
POST /auth/login/admin
{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```
→ **Copier le token JWT**

**2.2 Autoriser dans Swagger**
- Cliquer sur "Authorize" 🔓
- Coller le token
- Cliquer "Authorize" puis "Close"

**2.3 Valider le Producteur**
```
PUT /admin/producteur/validation/{id}?decision=ACCEPTE
```
→ 🔔 **Notification envoyée au producteur : "Compte validé"**

---

### ÉTAPE 3 : Upload de la Photo du Producteur

**3.1 Upload de la photo**
```
POST /files/upload
[Sélectionner une image]
```

**Réponse :**
```json
{
  "fileName": "uuid.jpg",
  "fileDownloadUri": "http://localhost:8080/suguconnect/files/download/uuid.jpg"
}
```

**3.2 Mettre à jour le producteur avec l'URL**
```
PUT /producteur/{id}
{
  "photoUrl": "http://localhost:8080/suguconnect/files/download/uuid.jpg"
}
```

---

### ÉTAPE 4 : Ajouter des Produits

**4.1 Login Producteur**
```
POST /auth/login/producteur
{
  "telephone": "77777777",
  "motDePasse": "password123"
}
```
→ **Copier le nouveau token et re-autoriser**

**4.2 Ajouter un produit avec photos**
```
POST /producteur/{producteurId}/produit
- nom: Mangues Bio
- description: Mangues fraîches
- prixUnitaire: 2500
- unite: KILOGRAMME
- quantite: 100
- categorieId: 1
- photos: [Sélectionner 2-3 images]
```

---

### ÉTAPE 5 : Consommateur Passe Commande

**5.1 Login Consommateur**
```
POST /auth/login/consommateur
{
  "telephone": "76543210",
  "motDePasse": "password123"
}
```
→ **Copier le token et re-autoriser**

**5.2 Ajouter produit au panier**
```
POST /consommateur/1/panier/ajouter/1?quantite=5
```

**5.3 Passer commande**
```
POST /consommateur/1/commande?modePaiement=ESPECES
```

**Résultat :**
- 🔔 **Notification consommateur :** "Commande passée"
- 🔔 **Notification producteur :** "Nouvelle commande reçue"

**→ Noter l'ID de la commande (ex: 15)**

---

### ÉTAPE 6 : Producteur Valide la Commande

**6.1 Re-login Producteur**
```
POST /auth/login/producteur
{
  "telephone": "77777777",
  "motDePasse": "password123"
}
```

**6.2 Vérifier les notifications**
```
GET /notifications/non-lues?utilisateurId=5
```
→ Vous devriez voir la notification "Nouvelle commande"

**6.3 Valider la commande**
```
PUT /producteur/commande/15/statut
?producteurId=5
&nouveauStatut=VALIDEE
```

**Résultat :**
- 🔔 **Notification consommateur :** "Commande validée"

---

### ÉTAPE 7 : Producteur Met en Livraison

```
PUT /producteur/commande/15/statut
?producteurId=5
&nouveauStatut=EN_LIVRAISON
```

**Résultat :**
- 🔔 **Notification consommateur :** "Commande en cours de livraison"

---

### ÉTAPE 8 : Producteur Marque comme Livrée

```
PUT /producteur/commande/15/statut
?producteurId=5
&nouveauStatut=LIVREE
```

**Résultat :**
- 🔔 **Notification consommateur :** "Commande livrée - Veuillez valider la réception"

**🔒 IMPORTANT :** Seul le producteur peut mettre ce statut !

---

### ÉTAPE 9 : Consommateur Valide la Réception

**9.1 Re-login Consommateur**
```
POST /auth/login/consommateur
{
  "telephone": "76543210",
  "motDePasse": "password123"
}
```

**9.2 Vérifier les notifications**
```
GET /notifications/non-lues?utilisateurId=1
```
→ Vous devriez voir "Commande livrée"

**9.3 Valider la réception**
```
POST /consommateur/commande/15/valider-reception
?consommateurId=1
```

**Résultat :**
- ✅ Réception validée
- 🔔 **Notification producteur :** "Réception validée par le client + Revenu de XXX FCFA"

---

### ÉTAPE 10 : Consommateur Donne un Avis

```
POST /avis/commande/15
?consommateurId=1
&note=5
&commentaire=Excellente qualité, produits frais et livraison rapide!
```

**Résultat :**
- ✅ Avis enregistré

**Voir les avis du producteur :**
```
GET /avis/producteur/5
GET /avis/producteur/5/moyenne
```

---

## 🔔 Récapitulatif des Notifications

### Notifications Consommateur :
1. ✅ Commande passée
2. ✅ Commande validée
3. ✅ Commande en livraison
4. ✅ Commande livrée

### Notifications Producteur :
1. ✅ Nouvelle commande reçue
2. ✅ Réception validée par client + Revenu

---

## 🧪 Test de Refus de Commande

### Producteur Refuse une Commande

```
PUT /producteur/commande/16/statut
?producteurId=5
&nouveauStatut=REFUSEE
&motifRejet=Stock insuffisant
```

**Résultat :**
- 🔔 **Notification consommateur :** "Commande refusée - Motif: Stock insuffisant"

---

## 📊 Vérifier Toutes les Notifications

### Pour un utilisateur spécifique :

```bash
# Notifications non lues
GET /notifications/non-lues?utilisateurId=1

# Toutes les notifications (7 derniers jours)
GET /notifications/recentes?utilisateurId=1

# Compter les non lues
GET /notifications/count-non-lues?utilisateurId=1

# Marquer comme lue
PUT /notifications/1/marquer-lue

# Marquer toutes comme lues
PUT /notifications/marquer-toutes-lues?utilisateurId=1
```

---

## 🎯 Points Clés de Sécurité

### ✅ Validations Implémentées

1. **Changement de statut :**
   - ✅ Seul le producteur propriétaire peut changer le statut
   - ✅ Motif obligatoire si REFUSEE

2. **Validation réception :**
   - ✅ Seul le consommateur propriétaire peut valider
   - ✅ Commande doit être au statut LIVREE
   - ✅ Validation unique (pas de double validation)

3. **Avis :**
   - ✅ Possible uniquement après validation de réception
   - ✅ Un seul avis par commande
   - ✅ Note entre 1 et 5

---

## 📱 Exemple Frontend JavaScript

### Valider Réception + Donner Avis

```javascript
// 1. Valider la réception
async function validerReception(commandeId, consommateurId, token) {
    const response = await fetch(
        `http://localhost:8080/suguconnect/consommateur/commande/${commandeId}/valider-reception?consommateurId=${consommateurId}`,
        {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        }
    );
    
    if (response.ok) {
        console.log('Réception validée !');
        // Afficher formulaire d'avis
        afficherFormulaireAvis(commandeId);
    }
}

// 2. Donner un avis
async function donnerAvis(commandeId, consommateurId, note, commentaire, token) {
    const response = await fetch(
        `http://localhost:8080/suguconnect/avis/commande/${commandeId}?consommateurId=${consommateurId}&note=${note}&commentaire=${encodeURIComponent(commentaire)}`,
        {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        }
    );
    
    if (response.ok) {
        console.log('Merci pour votre avis !');
    }
}

// 3. Producteur change statut
async function changerStatut(commandeId, producteurId, statut, token, motif = null) {
    let url = `http://localhost:8080/suguconnect/producteur/commande/${commandeId}/statut?producteurId=${producteurId}&nouveauStatut=${statut}`;
    
    if (motif) {
        url += `&motifRejet=${encodeURIComponent(motif)}`;
    }
    
    const response = await fetch(url, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.ok) {
        console.log('Statut mis à jour !');
    }
}
```

---

## 🎉 Résultat Final

Après avoir suivi toutes les étapes :

✅ Producteur créé avec photo et nom de ferme  
✅ Notifications envoyées à chaque action  
✅ Commande validée/livrée par le producteur  
✅ Réception validée par le consommateur  
✅ Avis donné par le consommateur  
✅ Historique complet des notifications  

**Toutes vos fonctionnalités sont opérationnelles ! 🚀**

---

## 📖 Documentation Détaillée

Pour plus d'informations, consultez :
- **NOUVELLES_FONCTIONNALITES.md** - Documentation complète
- **GUIDE_UPLOAD_FICHIERS.md** - Guide upload de fichiers
- **TEST_UPLOAD_RAPIDE.md** - Tests rapides upload

---

## 🆘 En Cas de Problème

### Erreur : "Vous n'avez pas de droit pour ajouter un produit"
**Solution :** Le producteur doit être validé (ACCEPTE) par l'admin

### Erreur : "Vous ne pouvez valider que les commandes livrées"
**Solution :** Le producteur doit d'abord mettre le statut à LIVREE

### Erreur : "Vous devez d'abord valider la réception de la commande"
**Solution :** Valider la réception avant de donner un avis

### 403 Forbidden
**Solution :** Vérifier que le token JWT est bien présent et valide

---

**Bon test ! 🎊**
