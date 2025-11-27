import '../config/api_config.dart';
import 'api_service.dart';

class PaiementService {
  final ApiService _apiService;

  PaiementService(this._apiService);

  // Récupérer tous les paiements
  Future<List<dynamic>> getPaiements() async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        ApiConfig.paiement,
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer un paiement par ID
  Future<Map<String, dynamic>> getPaiement(int id) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.paiement}/$id',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer le paiement d'une commande
  Future<Map<String, dynamic>> getPaiementParCommande(int commandeId) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.paiement}/commande/$commandeId',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Valider un paiement
  Future<Map<String, dynamic>> validerPaiement(int id, {String? referenceTransaction}) async {
    try {
      final response = await _apiService.put<Map<String, dynamic>>(
        '${ApiConfig.paiement}/$id/valider',
        queryParameters: referenceTransaction != null
            ? {'referenceTransaction': referenceTransaction}
            : null,
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Marquer un paiement comme échoué
  Future<Map<String, dynamic>> marquerPaiementEchoue(int id, String motifEchec) async {
    try {
      final response = await _apiService.put<Map<String, dynamic>>(
        '${ApiConfig.paiement}/$id/echouer',
        queryParameters: {'motifEchec': motifEchec},
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Rembourser un paiement
  Future<Map<String, dynamic>> rembourserPaiement(int id, String motifRemboursement) async {
    try {
      final response = await _apiService.put<Map<String, dynamic>>(
        '${ApiConfig.paiement}/$id/rembourser',
        queryParameters: {'motifRemboursement': motifRemboursement},
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Initier un paiement mobile
  Future<Map<String, dynamic>> initierPaiementMobile(int id, String numeroTelephone) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.paiement}/$id/initier-mobile',
        queryParameters: {'numeroTelephone': numeroTelephone},
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Webhook pour Orange Money / Wave
  Future<String> webhookPaiement(Map<String, String> payload) async {
    try {
      final response = await _apiService.post<String>(
        '${ApiConfig.paiement}/webhook',
        data: payload,
      );
      return response.data ?? 'Webhook traité avec succès';
    } catch (e) {
      rethrow;
    }
  }
}


