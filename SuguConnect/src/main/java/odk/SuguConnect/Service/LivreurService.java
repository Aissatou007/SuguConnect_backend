package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.LivreurRequestDTO;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Entity.Livreur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Mapper.LivreurMapper;
import odk.SuguConnect.Repository.LivreurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivreurService {
    private final LivreurRepository livreurRepository;
    private final PasswordEncoder passwordEncoder;

    public LivreurResponseDTO creerLivreur(LivreurRequestDTO dto) {
        // Vérifier si le livreur existe déjà
        if (livreurRepository.findByMatricule(dto.getMatricule()) != null) {
            throw new IllegalArgumentException("Un livreur avec cette matricule existe déjà");
        }
        
        Livreur livreur = LivreurMapper.toEntity(dto, new Livreur());
        livreur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        livreur.setRole(Role.LIVREUR); // S'assurer que le rôle est correctement défini
        livreur.setDateInscription(LocalDate.now());
        livreur.setActif(true);
        
        Livreur saved = livreurRepository.save(livreur);
        return LivreurMapper.toResponse(saved);
    }
    
    public List<LivreurResponseDTO> recupererTousLesLivreurs() {
        return livreurRepository.findAll().stream()
                .map(LivreurMapper::toResponse)
                .toList();
    }
    
    public LivreurResponseDTO recupererUnLivreur(int id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
        return LivreurMapper.toResponse(livreur);
    }
    
    public LivreurResponseDTO modifierLivreur(LivreurRequestDTO dto, int id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
        
        // Vérifier si la matricule est déjà utilisée par un autre livreur
        Livreur existing = livreurRepository.findByMatricule(dto.getMatricule());
        if (existing != null && existing.getId() != id) {
            throw new IllegalArgumentException("Cette matricule est déjà utilisée par un autre livreur");
        }
        
        LivreurMapper.toEntity(dto, livreur);
        
        // Encoder le mot de passe uniquement s'il est fourni
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isEmpty()) {
            livreur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }
        
        Livreur saved = livreurRepository.save(livreur);
        return LivreurMapper.toResponse(saved);
    }
    
    public String supprimerLivreur(int id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
        livreurRepository.delete(livreur);
        return "Le livreur a été supprimé avec succès";
    }
    
    public List<LivreurResponseDTO> recupererLivreursDisponibles() {
        return livreurRepository.findByDisponibleTrue().stream()
                .map(LivreurMapper::toResponse)
                .toList();
    }
    
    public LivreurResponseDTO mettreAJourDisponibilite(int id, boolean disponible) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
        livreur.setDisponible(disponible);
        Livreur saved = livreurRepository.save(livreur);
        return LivreurMapper.toResponse(saved);
    }
}