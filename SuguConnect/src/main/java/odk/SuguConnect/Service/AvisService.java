package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Responses.AvisResponseDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;
    private final CommandeRepository commandeRepository;
    private final ConsommateurRepository consommateurRepository;
    private final ProducteurRepository producteurRepository;

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
        avis.setValide(true);

        return avisRepository.save(avis);
    }


    public List<Avis> getAvisProducteur(int producteurId) {
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé"));
        return avisRepository.findByProducteurAndValideTrue(producteur);
    }

    public List<Avis> getAllAvis() {
        return avisRepository.findAll();
    }
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

    public Avis getAvisCommande(int commandeId) {
        List<Avis> avis = avisRepository.findByCommandeIdCommande(commandeId);
        return avis.isEmpty() ? null : avis.get(0);
    }
    public List<AvisResponseDTO> getAllAvisDTO() {
        List<Avis> avisList = avisRepository.findAll();

        return avisList.stream().map(avis -> {
            AvisResponseDTO dto = new AvisResponseDTO();
            dto.setId((long) avis.getId());
            dto.setNote(avis.getNote());
            dto.setCommentaire(avis.getCommentaire());
            dto.setDateAvis(avis.getDateAvis() != null ? avis.getDateAvis().toString() : null);
            dto.setValide(avis.isValide());

            // ✅ Consommateur
            if (avis.getConsommateur() != null) {
                AvisResponseDTO.ConsommateurDTO consommateurDTO = new AvisResponseDTO.ConsommateurDTO();
                consommateurDTO.setId((long) avis.getConsommateur().getId());
                consommateurDTO.setPrenom(avis.getConsommateur().getPrenom());
                consommateurDTO.setNom(avis.getConsommateur().getNom());
                consommateurDTO.setEmail(avis.getConsommateur().getEmail());
                dto.setConsommateur(consommateurDTO);
            }

            // ✅ Producteur
            if (avis.getProducteur() != null) {
                AvisResponseDTO.ProducteurDTO producteurDTO = new AvisResponseDTO.ProducteurDTO();
                producteurDTO.setId((long) avis.getProducteur().getId());
                producteurDTO.setPrenom(avis.getProducteur().getPrenom());
                producteurDTO.setNom(avis.getProducteur().getNom());
                producteurDTO.setNomFerme(avis.getProducteur().getNomFerme());
                producteurDTO.setEmail(avis.getProducteur().getEmail());
                dto.setProducteur(producteurDTO);
            } else if (avis.getCommande() != null && avis.getCommande().getCommandeProduits() != null
                    && !avis.getCommande().getCommandeProduits().isEmpty()) {
                // fallback via le produit de la commande
                var commandeProduit = avis.getCommande().getCommandeProduits().get(0);
                if (commandeProduit.getProduit() != null && commandeProduit.getProduit().getProducteur() != null) {
                    Producteur p = commandeProduit.getProduit().getProducteur();
                    AvisResponseDTO.ProducteurDTO producteurDTO = new AvisResponseDTO.ProducteurDTO();
                    producteurDTO.setId((long) p.getId());
                    producteurDTO.setPrenom(p.getPrenom());
                    producteurDTO.setNom(p.getNom());
                    producteurDTO.setNomFerme(p.getNomFerme());
                    producteurDTO.setEmail(p.getEmail());
                    dto.setProducteur(producteurDTO);
                }
            }

            // ✅ Commande
            if (avis.getCommande() != null) {
                AvisResponseDTO.CommandeDTO commandeDTO = new AvisResponseDTO.CommandeDTO();
                commandeDTO.setId((long) avis.getCommande().getIdCommande());
                commandeDTO.setReference("CMD-" + avis.getCommande().getIdCommande()); // ou autre référence
                dto.setCommande(commandeDTO);
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
