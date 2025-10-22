package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Notification;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Interface.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // Récupérer toutes les notifications d'un utilisateur
    List<Notification> findByDestinataire(Utilisateur destinataire);
    
    // Récupérer les notifications non lues d'un utilisateur
    List<Notification> findByDestinataireAndLuFalse(Utilisateur destinataire);
    
    // Récupérer les notifications par type
    List<Notification> findByTypeMessage(TypeMessage typeMessage);
    
    // Récupérer les notifications d'un utilisateur par type
    List<Notification> findByDestinataireAndTypeMessage(Utilisateur destinataire, TypeMessage typeMessage);
    
    // Récupérer les notifications non expirées d'un utilisateur
    @Query("SELECT n FROM Notification n WHERE n.destinataire = :destinataire AND (n.dateExpiration IS NULL OR n.dateExpiration > :now) ORDER BY n.dateEnvoi DESC")
    List<Notification> findNotificationsNonExpirees(@Param("destinataire") Utilisateur destinataire, @Param("now") LocalDateTime now);
    
    // Récupérer les notifications expirées
    @Query("SELECT n FROM Notification n WHERE n.dateExpiration IS NOT NULL AND n.dateExpiration < :now")
    List<Notification> findNotificationsExpirees(@Param("now") LocalDateTime now);
    
    // Compter les notifications non lues
    long countByDestinataireAndLuFalse(Utilisateur destinataire);
    
    // Récupérer les notifications récentes (7 derniers jours)
    @Query("SELECT n FROM Notification n WHERE n.destinataire = :destinataire AND n.dateEnvoi >= :dateDebut ORDER BY n.dateEnvoi DESC")
    List<Notification> findNotificationsRecentes(@Param("destinataire") Utilisateur destinataire, @Param("dateDebut") LocalDateTime dateDebut);
}
