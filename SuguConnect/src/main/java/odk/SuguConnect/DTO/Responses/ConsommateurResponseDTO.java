package odk.SuguConnect.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Role;

import java.time.LocalDate;

@Schema(description = "Informations d'un consommateur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConsommateurResponseDTO(
        @Schema(description = "ID du consommateur", example = "1")
        int id,

        @Schema(description = "Nom du consommateur", example = "Dembele")
        String nom,

        @Schema(description = "Prénom du consommateur", example = "Adama")
        String prenom,

        @Schema(description = "Numéro de téléphone", example = "94907946")
        String telephone,

        @Schema(description = "Email du consommateur", example = "adama@example.com")
        String email,

        @Schema(description = "Localisation du consommateur", example = "Bamako")
        String localisation,

        @Schema(description = "Latitude de la position")
        long latitude,

        @Schema(description = "Longitude de la position")
        long longitude,

        @Schema(description = "Rôle de l'utilisateur")
        Role role,

        @Schema(description = "ID du panier associé", example = "1")
        Integer panierId,

        @Schema(description = "Date d'inscription")
        LocalDate dateInscription,

        @Schema(description = "Nombre de commandes effectuées")
        int nombreCommandes
) { }
