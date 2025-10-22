package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Admin findByTelephone(String telephone);
    Optional <Admin> findByEmail(String email);
    boolean existsByEmail(String email);
}
