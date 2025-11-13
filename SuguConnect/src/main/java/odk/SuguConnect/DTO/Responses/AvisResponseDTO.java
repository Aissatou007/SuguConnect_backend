package odk.SuguConnect.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvisResponseDTO {
    private Long id;
    private int note;
    private String commentaire;
    private String dateAvis;
    private boolean valide;

    private ConsommateurDTO consommateur;
    private ProducteurDTO producteur;
    private CommandeDTO commande;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConsommateurDTO {
        private Long id;
        private String prenom;
        private String nom;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProducteurDTO {
        private Long id;
        private String prenom;
        private String nom;
        private String nomFerme;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommandeDTO {
        private Long id;
        private String reference;
    }
}

