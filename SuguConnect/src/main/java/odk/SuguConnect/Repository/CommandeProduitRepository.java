package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.CommandeProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeProduitRepository extends JpaRepository<CommandeProduit, Integer> {
}