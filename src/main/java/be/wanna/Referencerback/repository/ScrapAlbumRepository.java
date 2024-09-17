package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.album.ScrapAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapAlbumRepository extends JpaRepository<ScrapAlbum, Long> {
    @Query("Select alb from ScrapAlbum alb where alb.code = :code and alb.author.name = :author and alb.provider.name = :provider")
    Optional<ScrapAlbum> findByCodeAndAuthorAndProvider(String code, String author, String provider);

    @Query("Select alb from ScrapAlbum alb where alb.code = :code and alb.provider.name = :provider")
    Optional<ScrapAlbum> findByCodeAndProvider(String code, String provider);
}
