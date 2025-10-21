package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie , Integer> {
boolean existsByLabelle(String labelle);
}
