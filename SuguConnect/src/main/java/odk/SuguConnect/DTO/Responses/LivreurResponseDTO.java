package odk.SuguConnect.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LivreurResponseDTO {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String localisation;
    private String matricule;
    private String vehicule;
    private boolean disponible;
}