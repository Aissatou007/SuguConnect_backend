import '../config/api_config.dart';
import '../models/auth_models.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _apiService;

  AuthService(this._apiService);

  // Connexion universelle
  Future<AuthResponse> login(LoginRequest request) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.auth}/login',
        data: request.toJson(),
      );

      final authResponse = AuthResponse.fromJson(response.data!);
      await _apiService.saveToken(authResponse.token);
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  // Connexion admin
  Future<AuthResponse> loginAdmin(LoginRequest request) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.auth}/login/admin',
        data: request.toJson(),
      );

      final authResponse = AuthResponse.fromJson(response.data!);
      await _apiService.saveToken(authResponse.token);
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  // Connexion producteur
  Future<AuthResponse> loginProducteur(LoginRequest request) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.auth}/login/producteur',
        data: request.toJson(),
      );

      final authResponse = AuthResponse.fromJson(response.data!);
      await _apiService.saveToken(authResponse.token);
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  // Connexion consommateur
  Future<AuthResponse> loginConsommateur(LoginRequest request) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.auth}/login/consommateur',
        data: request.toJson(),
      );

      final authResponse = AuthResponse.fromJson(response.data!);
      await _apiService.saveToken(authResponse.token);
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  // Valider un token
  Future<Map<String, dynamic>> validateToken(String token, String telephone) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.auth}/validate-token',
        queryParameters: {
          'token': token,
          'telephone': telephone,
        },
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Obtenir les infos de l'utilisateur connecté
  Future<UserInfo> getCurrentUser() async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.auth}/me',
      );
      return UserInfo.fromJson(response.data!);
    } catch (e) {
      rethrow;
    }
  }

  // Déconnexion
  Future<void> logout() async {
    await _apiService.clearAuth();
  }

  // Vérifier si l'utilisateur est connecté
  Future<bool> isAuthenticated() async {
    final token = await _apiService.getToken();
    return token != null && token.isNotEmpty;
  }
}


