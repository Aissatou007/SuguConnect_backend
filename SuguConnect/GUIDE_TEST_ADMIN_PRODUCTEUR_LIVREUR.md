# Guide de Test : Création de Producteurs et Gestion des Livreurs par l'Administrateur

## 1. Création de Producteurs (Admin uniquement)

### Endpoint
```
POST /admin/producteurs/ajouter
```

### Description
Seul un administrateur peut créer un nouveau producteur. Cette restriction a été mise en place pour renforcer la sécurité et le contrôle sur les comptes producteurs.

### Prérequis
- Être authentifié avec un compte administrateur
- Avoir un token JWT valide avec le rôle ADMIN

### Procédure de test avec Swagger
1. Connectez-vous via l'endpoint `/auth/login/admin` pour obtenir un token JWT
2. Cliquez sur le bouton "Authorize" en haut de la page Swagger
3. Entrez : `Bearer VOTRE_TOKEN_JWT`
4. Accédez à la section "Administrateur"
5. Utilisez l'endpoint `POST /admin/producteurs/ajouter`
6. Remplissez les informations requises du producteur
7. Exécutez la requête

### Exemple de corps de requête
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "telephone": "123456789",
  "motDePasse": "motdepasse123",
  "localisation": "Dakar"
}
```

## 2. Visualisation des Livreurs pour une Commande (Admin uniquement)

### Endpoint
```
GET /admin/commandes/livreurs
```

### Description
Permet à un administrateur de voir la liste des livreurs disponibles lorsqu'il crée ou gère une commande.

### Prérequis
- Être authentifié avec un compte administrateur
- Avoir un token JWT valide avec le rôle ADMIN

### Procédure de test avec Swagger
1. Connectez-vous via l'endpoint `/auth/login/admin` pour obtenir un token JWT
2. Cliquez sur le bouton "Authorize" en haut de la page Swagger
3. Entrez : `Bearer VOTRE_TOKEN_JWT`
4. Accédez à la section "Administrateur"
5. Utilisez l'endpoint `GET /admin/commandes/livreurs`
6. Exécutez la requête

### Réponse attendue
```json
[
  {
    "id": 1,
    "nom": "Martin",
    "prenom": "Pierre",
    "matricule": "LIV001",
    "disponible": true
  },
  {
    "id": 2,
    "nom": "Bernard",
    "prenom": "Sophie",
    "matricule": "LIV002",
    "disponible": true
  }
]
```

## 3. Test de l'Inscription Public des Producteurs (Doit être refusée)

### Endpoint
```
POST /producteur/inscription
```

### Description
Cette route était auparavant accessible publiquement mais est maintenant restreinte aux administrateurs uniquement.

### Test attendu
- Si un utilisateur non authentifié ou non admin tente d'accéder à cette route, il doit recevoir une réponse 403 (Forbidden)

### Procédure de test
1. Essayez d'accéder à l'endpoint sans authentification
2. Essayez d'accéder à l'endpoint avec un compte non admin (consommateur ou producteur)
3. Vérifiez que la réponse est 403 Forbidden

## 4. Rôles et Permissions Résumés

| Action | Rôle Requis | Endpoint |
|--------|-------------|----------|
| Créer un producteur | ADMIN | POST /admin/producteurs/ajouter |
| Voir les livreurs pour commande | ADMIN | GET /admin/commandes/livreurs |
| Inscription producteur (publique) | ADMIN uniquement | POST /producteur/inscription |

## 5. Messages d'Erreur Attendus

### Tentative d'accès sans authentification
```
401 Unauthorized
```

### Tentative d'accès avec un rôle non autorisé
```
403 Forbidden
```

### Données invalides
```
400 Bad Request
{
  "message": "Données invalides"
}
```

## 6. Bonnes Pratiques de Test

1. **Testez toujours avec différents rôles** : ADMIN, CONSOMMATEUR, PRODUCTEUR
2. **Testez sans authentification** : Vérifiez que les routes protégées retournent 401
3. **Testez avec des données valides et invalides** : Vérifiez la validation des entrées
4. **Testez les cas limites** : Producteur déjà existant, livreurs indisponibles, etc.