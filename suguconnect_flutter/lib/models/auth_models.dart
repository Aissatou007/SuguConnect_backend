import 'enums.dart';

// Login Request
class LoginRequest {
  final String telephone;
  final String motDePasse;

  LoginRequest({
    required this.telephone,
    required this.motDePasse,
  });

  Map<String, dynamic> toJson() {
    return {
      'telephone': telephone,
      'motDePasse': motDePasse,
    };
  }

  factory LoginRequest.fromJson(Map<String, dynamic> json) {
    return LoginRequest(
      telephone: json['telephone'] as String,
      motDePasse: json['motDePasse'] as String,
    );
  }
}

// Auth Response
class AuthResponse {
  final String token;
  final String tokenType;
  final int userId;
  final String nom;
  final String prenom;
  final String email;
  final String telephone;
  final Role role;
  final String message;

  AuthResponse({
    required this.token,
    this.tokenType = 'Bearer',
    required this.userId,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.telephone,
    required this.role,
    this.message = 'Connexion réussie',
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      tokenType: json['tokenType'] as String? ?? 'Bearer',
      userId: json['userId'] as int,
      nom: json['nom'] as String? ?? '',
      prenom: json['prenom'] as String? ?? '',
      email: json['email'] as String? ?? '',
      telephone: json['telephone'] as String,
      role: Role.fromString(json['role'] as String?) ?? Role.CONSOMMATEUR,
      message: json['message'] as String? ?? 'Connexion réussie',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'token': token,
      'tokenType': tokenType,
      'userId': userId,
      'nom': nom,
      'prenom': prenom,
      'email': email,
      'telephone': telephone,
      'role': role.value,
      'message': message,
    };
  }
}

// User Info (from /auth/me)
class UserInfo {
  final String telephone;
  final String role;
  final String? roleWithPrefix;
  final bool authenticated;
  final String? message;

  UserInfo({
    required this.telephone,
    required this.role,
    this.roleWithPrefix,
    required this.authenticated,
    this.message,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      telephone: json['telephone'] as String,
      role: json['role'] as String,
      roleWithPrefix: json['roleWithPrefix'] as String?,
      authenticated: json['authenticated'] as bool? ?? false,
      message: json['message'] as String?,
    );
  }
}


