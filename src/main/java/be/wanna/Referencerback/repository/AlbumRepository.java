package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("Select alb from Album alb where alb.code = :code and alb.author.name = :author and alb.provider.name = :provider")
    Optional<Album> findByCodeAndAuthorAndProvider(String code, String author, String provider);

    @Query("Select alb from Album alb where alb.code = :code and alb.provider.name = :provider")
    Optional<Album> findByCodeAndProvider(String code, String provider);

    Optional<Album> findAlbumByCode(String code);
}
