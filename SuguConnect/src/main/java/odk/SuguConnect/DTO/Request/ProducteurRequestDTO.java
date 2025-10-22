package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO pour l'inscription/modification d'un producteur")
public record ProducteurRequestDTO(
        @Schema(description = "Nom du producteur", example = "Diallo")
        String nom,
        @Schema(description = "Prénom du producteur", example = "Amadou")
        String prenom,
        @Schema(description = "Numéro de téléphone", example = "77123456")
        String telephone,
        @Schema(description = "Adresse email", example = "amadou@ferme.com")
        String email,
        @Schema(description = "Localisation", example = "Conakry")
        String localisation,
        @Schema(description = "Latitude")
        long latitude,
        @Schema(description = "Longitude")
        long longitude,
        @Schema(description = "Mot de passe")
        String motDePasse,
        @Schema(description = "Description de l'activité")
        String description,
        @Schema(description = "Nom de la ferme", example = "Ferme Bio d'Amadou")
        String nomFerme
) {
}
