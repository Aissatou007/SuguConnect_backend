package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Avis;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Repository.AvisRepository;
import odk.SuguConnect.Repository.CommandeRepository;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;
    private final CommandeRepository commandeRepository;
    private final ConsommateurRepository consommateurRepository;
    private final ProducteurRepository producteurRepository;

    /**
     * Créer un avis après validation de réception
     */
    @Transactional
    public Avis creerAvis(int commandeId, int consommateurId, int note, String commentaire) {
        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée"));

        // Vérifier que le consommateur est bien le propriétaire de la commande
        if (commande.getConsommateur().getId() != consommateurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à donner un avis sur cette commande");
        }

        // Vérifier que la commande est livrée
        if (commande.getStatutCommande() != StatutCommande.LIVREE) {
            throw new IllegalStateException("Vous ne pouvez donner un avis qu'après livraison de la commande");
        }

        // Vérifier que la réception a été validée
        if (!commande.isReceptionValidee()) {
            throw new IllegalStateException("Vous devez d'abord valider la réception de la commande");
        }

        // Vérifier qu'un avis n'existe pas déjà
        if (avisRepository.existsByCommandeIdCommande(commandeId)) {
            throw new IllegalStateException("Vous avez déjà donné un avis sur cette commande");
        }

        // Valider la note (1-5)
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5");
        }

        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur non trouvé"));

        // Récupérer le producteur depuis le premier produit de la commande
        Producteur producteur = commande.getCommandeProduits().get(0).getProduit().getProducteur();

        Avis avis = new Avis();
        avis.setCommande(commande);
        avis.setConsommateur(consommateur);
        avis.setProducteur(producteur);
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        avis.setDateAvis(LocalDateTime.now());
        avis.setValide(true); // Auto-validation (ou false si modération par admin requise)

        return avisRepository.save(avis);
    }

    /**
     * Récupérer tous les avis validés d'un producteur
     */
    public List<Avis> getAvisProducteur(int producteurId) {
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé"));
        return avisRepository.findByProducteurAndValideTrue(producteur);
    }

    /**
     * Calculer la moyenne des notes d'un producteur
     */
    public double getMoyenneNotesProducteur(int producteurId) {
        List<Avis> avis = getAvisProducteur(producteurId);
        if (avis.isEmpty()) {
            return 0.0;
        }
        return avis.stream()
                .mapToInt(Avis::getNote)
                .average()
                .orElse(0.0);
    }

    /**
     * Récupérer l'avis d'une commande
     */
    public Avis getAvisCommande(int commandeId) {
        List<Avis> avis = avisRepository.findByCommandeIdCommande(commandeId);
        return avis.isEmpty() ? null : avis.get(0);
    }
}
