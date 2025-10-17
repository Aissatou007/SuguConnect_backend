package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Mapper.AdminMapper;
import odk.SuguConnect.Repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private AdminRepository adminRepository ;
    private AdminRequestDTO adminRequestDTO;
    private AdminResponseDTO adminResponseDTO;

    public String inscriptionAdmin(AdminRequestDTO adminRequestDTO , String telephone){
        Admin u = adminRepository.findByTelephone(telephone);
        if(u != null){
            throw new IllegalArgumentException("Ce compte existe déja");
        }
        Admin admin = AdminMapper.toEntity(adminRequestDTO , new Admin());
        admin.setRole(Role.ADMIN);
        admin.setDateInscription(LocalDate.now());
        adminRepository.save(admin);
        return "Bienvenue Admin ";
    }

    public List<AdminResponseDTO> recupererLesAdmins(){
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(AdminMapper::toResponse).toList();
    }

    public AdminResponseDTO recupererUnAdmin(int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));
        return AdminMapper.toResponse(admin);
    }

    public String modifierInformationAdmin(AdminRequestDTO adminRequestDTO, int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));

        admin.setNom(adminRequestDTO.nom());
        admin.setPrenom(adminRequestDTO.prenom());
        admin.setTelephone(adminRequestDTO.telephone());
        admin.setEmail(adminRequestDTO.email());
        admin.setLocalisation(adminRequestDTO.localisation());
        admin.setMotDePasse(adminRequestDTO.motDePasse());

        adminRepository.save(admin);

        return "Les informations de l'admin ont été modifiées avec succès";
    }

    public String supprimerAdmin(int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));

        adminRepository.delete(admin);

        return "Le compte admin a été supprimé avec succès";
    }
}
