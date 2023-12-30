package be.wanna.Referencerback.repository.connection;

import be.wanna.Referencerback.entity.connection.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
