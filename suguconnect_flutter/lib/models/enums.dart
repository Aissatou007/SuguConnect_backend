// Enums correspondant aux enums Java du backend

enum Role {
  ADMIN,
  PRODUCTEUR,
  CONSOMMATEUR;

  String get value => name;
  
  static Role? fromString(String? value) {
    if (value == null) return null;
    try {
      return Role.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum StatutCommande {
  EN_ATTENTE,
  VALIDEE,
  REFUSEE,
  EN_LIVRAISON,
  LIVREE,
  ANNULEE;

  String get value => name;
  
  static StatutCommande? fromString(String? value) {
    if (value == null) return null;
    try {
      return StatutCommande.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum StatutPaiement {
  EN_ATTENTE,
  VALIDE,
  ECHOUE,
  REMBOURSE;

  String get value => name;
  
  static StatutPaiement? fromString(String? value) {
    if (value == null) return null;
    try {
      return StatutPaiement.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum StatutProducteur {
  EN_ATTENTE,
  VALIDE,
  REFUSE;

  String get value => name;
  
  static StatutProducteur? fromString(String? value) {
    if (value == null) return null;
    try {
      return StatutProducteur.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum ModePaiement {
  ESPECES,
  ORANGE_MONEY,
  WAVE,
  MOBILE_MONEY;

  String get value => name;
  
  static ModePaiement? fromString(String? value) {
    if (value == null) return null;
    try {
      return ModePaiement.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum ModeLivraison {
  DOMICILE,
  POINT_RELAIS,
  RETRAIT_SUR_PLACE;

  String get value => name;
  
  static ModeLivraison? fromString(String? value) {
    if (value == null) return null;
    try {
      return ModeLivraison.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum Unite {
  KG,
  G,
  L,
  ML,
  PIECE,
  BOTTE,
  SAC,
  CARTON;

  String get value => name;
  
  static Unite? fromString(String? value) {
    if (value == null) return null;
    try {
      return Unite.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}

enum StatutUtilisateur {
  ACTIF,
  INACTIF;

  String get value => name;
  
  static StatutUtilisateur? fromString(String? value) {
    if (value == null) return null;
    try {
      return StatutUtilisateur.values.firstWhere(
        (e) => e.name.toUpperCase() == value.toUpperCase(),
      );
    } catch (e) {
      return null;
    }
  }
}


