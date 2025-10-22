package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Notification;
import odk.SuguConnect.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "API de gestion des notifications utilisateurs")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/utilisateur/{utilisateurId}")
    @Operation(
            summary = "Récupérer les notifications d'un utilisateur",
            description = "Retourne toutes les notifications non expirées d'un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des notifications récupérée"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<Notification>> getNotifications(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int utilisateurId) {
        List<Notification> notifications = notificationService.getNotifications(utilisateurId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    @Operation(
            summary = "Récupérer les notifications non lues",
            description = "Retourne toutes les notifications non lues d'un utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des notifications non lues récupérée"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<Notification>> getNotificationsNonLues(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int utilisateurId) {
        List<Notification> notifications = notificationService.getNotificationsNonLues(utilisateurId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/utilisateur/{utilisateurId}/recentes")
    @Operation(
            summary = "Récupérer les notifications récentes",
            description = "Retourne les notifications des 7 derniers jours"
    )
    @ApiResponse(responseCode = "200", description = "Notifications récentes récupérées")
    public ResponseEntity<List<Notification>> getNotificationsRecentes(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int utilisateurId) {
        List<Notification> notifications = notificationService.getNotificationsRecentes(utilisateurId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/utilisateur/{utilisateurId}/count-non-lues")
    @Operation(
            summary = "Compter les notifications non lues",
            description = "Retourne le nombre de notifications non lues d'un utilisateur"
    )
    @ApiResponse(responseCode = "200", description = "Nombre de notifications non lues")
    public ResponseEntity<Long> compterNotificationsNonLues(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int utilisateurId) {
        long count = notificationService.compterNotificationsNonLues(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/marquer-lue")
    @Operation(
            summary = "Marquer une notification comme lue",
            description = "Change le statut d'une notification à 'lue'"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marquée comme lue"),
            @ApiResponse(responseCode = "404", description = "Notification non trouvée")
    })
    public ResponseEntity<Notification> marquerCommeLue(
            @Parameter(description = "ID de la notification", required = true)
            @PathVariable int notificationId) {
        Notification notification = notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/utilisateur/{utilisateurId}/marquer-toutes-lues")
    @Operation(
            summary = "Marquer toutes les notifications comme lues",
            description = "Marque toutes les notifications d'un utilisateur comme lues"
    )
    @ApiResponse(responseCode = "200", description = "Toutes les notifications marquées comme lues")
    public ResponseEntity<String> marquerToutesCommeLues(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int utilisateurId) {
        notificationService.marquerToutesCommeLues(utilisateurId);
        return ResponseEntity.ok("Toutes les notifications ont été marquées comme lues");
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Supprimer une notification",
            description = "Supprime une notification spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Notification non trouvée")
    })
    public ResponseEntity<String> supprimerNotification(
            @Parameter(description = "ID de la notification", required = true)
            @PathVariable int notificationId) {
        notificationService.supprimerNotification(notificationId);
        return ResponseEntity.ok("Notification supprimée avec succès");
    }
}
