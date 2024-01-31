package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, String> {
    @Query("Select a from Author a where a.name = :name and a.provider.name = :provider")
    Optional<Author> findAuthorByNameAndProvider(String name, String provider);
}
