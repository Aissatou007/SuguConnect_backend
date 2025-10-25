package odk.SuguConnect.DTO.Responses;

import lombok.Builder;

@Builder
public record LivreurResponseDTO(
        int id,
        String nom,
        String prenom,
        String telephone,
        String email,
        String localisation,
        String matricule,
        String vehicule,
        boolean disponible
) {
}