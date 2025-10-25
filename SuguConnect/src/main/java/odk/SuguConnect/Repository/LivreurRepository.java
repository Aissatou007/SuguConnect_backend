package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivreurRepository extends JpaRepository<Livreur, Integer> {
    List<Livreur> findByDisponibleTrue();
    Livreur findByMatricule(String matricule);
}