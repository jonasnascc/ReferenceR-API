package be.wanna.Referencerback.repository.connection;

import be.wanna.Referencerback.entity.connection.CsrfToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsrfTokenRepository extends JpaRepository<CsrfToken, String> {
}
