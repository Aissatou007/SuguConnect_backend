package odk.SuguConnect.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LivreurRequestDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String localisation;
    private String motDePasse;
    private String matricule;
    private String vehicule;
}