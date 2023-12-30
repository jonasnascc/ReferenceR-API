package be.wanna.Referencerback.repository.connection;

import be.wanna.Referencerback.entity.connection.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookieRepository extends JpaRepository<Cookie, String> {
}
