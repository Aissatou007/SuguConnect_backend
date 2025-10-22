# Système de Notifications - SuguConnect

## 📬 Vue d'ensemble

Le système de notifications de SuguConnect permet d'informer les utilisateurs (consommateurs, producteurs, administrateurs) de toutes les activités importantes sur la plateforme. Chaque notification a une **action spécifique** et une **durée de vie** définie.

## 🔔 Types de Notifications

### 1. Notifications de Commande 📦

| Type | Destinataire | Durée de vie | Action |
|------|-------------|--------------|--------|
| `COMMANDE_PASSEE` | Consommateur | 30 jours | Voir la commande |
| `COMMANDE_VALIDEE` | Consommateur | 30 jours | Suivre la commande |
| `COMMANDE_REFUSEE` | Consommateur | 30 jours | Voir les détails |
| `COMMANDE_EN_LIVRAISON` | Consommateur | 7 jours | Suivre la livraison |
| `COMMANDE_LIVREE` | Consommateur | 7 jours | Confirmer la réception |

### 2. Notifications de Paiement 💰

| Type | Destinataire | Durée de vie | Action |
|------|-------------|--------------|--------|
| `PAIEMENT_RECU` | Consommateur | 30 jours | Voir le reçu |
| `REMBOURSEMENT_EFFECTUE` | Consommateur | 30 jours | - |
| `REVENU_PRODUCTEUR` | Producteur | 30 jours | Voir le détail des revenus |

### 3. Notifications de Compte 👤

| Type | Destinataire | Durée de vie | Action |
|------|-------------|--------------|--------|
| `COMPTE_VALIDE` | Producteur | 7 jours | Ajouter des produits |
| `COMPTE_REFUSE` | Producteur | 30 jours | - |

### 4. Notifications de Stock 📊

| Type | Destinataire | Durée de vie | Action |
|------|-------------|--------------|--------|
| `STOCK_FAIBLE` | Producteur | 3 jours | Réapprovisionner |
| `PRODUIT_EPUISE` | Producteur | 3 jours | Réapprovisionner |

### 5. Notifications Administrateur 🛡️

| Type | Destinataire | Durée de vie | Action |
|------|-------------|--------------|--------|
| `NOUVELLE_INSCRIPTION_PRODUCTEUR` | Admin | 7 jours | Valider le producteur |
| `NOUVELLE_COMMANDE` | Admin | 7 jours | Voir la commande |
| `ACTIVITE_SUSPECTE` | Admin | 7 jours | Vérifier l'activité |
| `INFO_SYSTEME` | Admin | 7 jours | - |

## 🎯 Fonctionnalités du Système

### Caractéristiques des Notifications

✅ **Durée de vie configurable** : Chaque notification expire après une période définie
✅ **Action spécifique** : Chaque notification peut avoir une action associée (URL, lien)
✅ **Statut de lecture** : Les notifications peuvent être marquées comme lues
✅ **Nettoyage automatique** : Les notifications expirées sont supprimées automatiquement
✅ **Filtrage avancé** : Par utilisateur, par type, par statut

### Durées de Vie par Défaut

- **Notifications urgentes** (stock épuisé, livraison) : 3-7 jours
- **Notifications importantes** (commandes, paiements) : 30 jours
- **Notifications informatives** : 7 jours

## 🚀 API Endpoints

### Récupérer les notifications

```http
GET /suguconnect/notifications/utilisateur/{utilisateurId}
```
Retourne toutes les notifications non expirées d'un utilisateur.

### Récupérer les notifications non lues

```http
GET /suguconnect/notifications/utilisateur/{utilisateurId}/non-lues
```
Retourne uniquement les notifications non lues.

### Récupérer les notifications récentes

```http
GET /suguconnect/notifications/utilisateur/{utilisateurId}/recentes
```
Retourne les notifications des 7 derniers jours.

### Compter les notifications non lues

```http
GET /suguconnect/notifications/utilisateur/{utilisateurId}/count-non-lues
```
Retourne le nombre de notifications non lues.

### Marquer une notification comme lue

```http
PUT /suguconnect/notifications/{notificationId}/marquer-lue
```
Change le statut d'une notification à "lue".

### Marquer toutes les notifications comme lues

```http
PUT /suguconnect/notifications/utilisateur/{utilisateurId}/marquer-toutes-lues
```
Marque toutes les notifications d'un utilisateur comme lues.

### Supprimer une notification

```http
DELETE /suguconnect/notifications/{notificationId}
```
Supprime définitivement une notification.

## 💻 Utilisation dans le Code

### Créer une notification de commande passée

```java
notificationService.notifierCommandePassee(consommateurId, commandeId, montant);
```

### Créer une notification de stock faible

```java
notificationService.notifierStockFaible(producteurId, produitId, nomProduit, stockRestant);
```

### Créer une notification de revenu producteur

```java
notificationService.notifierRevenuProducteur(producteurId, commandeId, montant);
```

### Créer une notification admin

```java
List<Integer> adminIds = Arrays.asList(1, 2, 3);
notificationService.notifierAdminNouvelleInscription(adminIds, producteurId, nomProducteur);
```

## 🔄 Nettoyage Automatique

Le système effectue un **nettoyage automatique** des notifications expirées **tous les jours à 2h du matin**.

Cette tâche planifiée supprime toutes les notifications dont la date d'expiration est dépassée, optimisant ainsi les performances de la base de données.

## 📊 Structure d'une Notification

```json
{
  "id": 1,
  "titre": "Commande passée",
  "message": "Votre commande #123 d'un montant de 15000 FCFA a été passée avec succès.",
  "typeMessage": "COMMANDE_PASSEE",
  "dateEnvoi": "2025-10-21T10:30:00",
  "dateExpiration": "2025-11-20T10:30:00",
  "lu": false,
  "action": "/commandes/123",
  "donneesSupplementaires": null,
  "destinataire": {
    "id": 5,
    "nom": "Kouadio",
    "prenom": "Marie"
  }
}
```

## 🎨 Intégration Frontend

### Badge de notifications non lues

```javascript
fetch('/suguconnect/notifications/utilisateur/5/count-non-lues')
  .then(response => response.json())
  .then(count => {
    document.getElementById('notification-badge').textContent = count;
  });
```

### Afficher les notifications

```javascript
fetch('/suguconnect/notifications/utilisateur/5/non-lues')
  .then(response => response.json())
  .then(notifications => {
    notifications.forEach(notif => {
      displayNotification(notif);
    });
  });
```

### Marquer comme lue au clic

```javascript
function markAsRead(notificationId) {
  fetch(`/suguconnect/notifications/${notificationId}/marquer-lue`, {
    method: 'PUT'
  })
  .then(response => response.json())
  .then(notification => {
    console.log('Notification marquée comme lue');
  });
}
```

## 📱 Scénarios d'Utilisation

### Scénario 1 : Commande complète

1. **Consommateur passe commande** → Notification `COMMANDE_PASSEE` + Admin notifié `NOUVELLE_COMMANDE`
2. **Admin/Producteur valide** → Notification `COMMANDE_VALIDEE`
3. **Commande en livraison** → Notification `COMMANDE_EN_LIVRAISON`
4. **Commande livrée** → Notification `COMMANDE_LIVREE` avec action "Confirmer réception"
5. **Consommateur confirme** → Notification `REVENU_PRODUCTEUR` au producteur

### Scénario 2 : Gestion de stock

1. **Stock atteint seuil (ex: 10 unités)** → Notification `STOCK_FAIBLE`
2. **Stock = 0** → Notification `PRODUIT_EPUISE`
3. **Producteur réapprovisionne** → Notifications supprimées automatiquement

### Scénario 3 : Validation producteur

1. **Producteur s'inscrit** → Admins reçoivent `NOUVELLE_INSCRIPTION_PRODUCTEUR`
2. **Admin valide** → Producteur reçoit `COMPTE_VALIDE` avec action "Ajouter produits"
3. **Admin refuse** → Producteur reçoit `COMPTE_REFUSE` avec raison

## 🔐 Sécurité et Bonnes Pratiques

✅ **Vérification des permissions** : Seul le destinataire peut voir ses notifications
✅ **Données sensibles** : Ne pas inclure de données sensibles dans les notifications
✅ **Actions validées** : Les actions doivent être validées côté serveur
✅ **Rate limiting** : Limiter le nombre de notifications pour éviter le spam
✅ **Nettoyage régulier** : Les notifications expirées sont automatiquement supprimées

## 📈 Statistiques et Monitoring

Le système permet de suivre :
- Nombre total de notifications par utilisateur
- Taux de lecture des notifications
- Types de notifications les plus fréquents
- Notifications expirées supprimées

## 🛠️ Configuration

### Modifier la durée de vie par défaut

Dans `NotificationService.java`, la durée par défaut est de **7 jours (168 heures)** :

```java
return creerNotification(destinataireId, typeMessage, message, action, 168);
```

### Modifier l'heure du nettoyage automatique

Dans `NotificationService.java`, modifier l'annotation `@Scheduled` :

```java
@Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h00
```

## 🎓 Exemples de Notifications

### Notification simple

```java
notificationService.creerNotification(
    userId, 
    TypeMessage.INFO_SYSTEME, 
    "Bienvenue sur SuguConnect !"
);
```

### Notification avec action

```java
notificationService.creerNotification(
    userId,
    TypeMessage.COMMANDE_PASSEE,
    "Votre commande #123 a été passée",
    "/commandes/123"
);
```

### Notification avec durée personnalisée

```java
notificationService.creerNotification(
    userId,
    TypeMessage.STOCK_FAIBLE,
    "Stock faible pour le produit X",
    "/produits/5/reapprovisionner",
    48 // 48 heures = 2 jours
);
```

---

## 📞 Support

Pour toute question sur le système de notifications :
- **Email** : support@suguconnect.com
- **Documentation API** : `http://localhost:8080/suguconnect/swagger-ui.html`

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025-10-21  
**Auteur** : Équipe SuguConnect
