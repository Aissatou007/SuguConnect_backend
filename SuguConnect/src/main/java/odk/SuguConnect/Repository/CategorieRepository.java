package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie , Integer> {
    boolean existsByLibelle(String libelle);
    Optional<Categorie> findByLibelle(String libelle);
}
