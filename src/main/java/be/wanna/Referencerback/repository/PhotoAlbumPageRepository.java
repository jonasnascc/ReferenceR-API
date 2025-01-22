package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.album.AlbumPhotosByPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoAlbumPageRepository extends JpaRepository<AlbumPhotosByPage, Long> {
    @Query("select ap from AlbumPhotosByPage ap where ap.page=:page and ap.album.id=:albumId")
    Optional<AlbumPhotosByPage> findByPageAndAlbumId(Integer page, Long albumId);

    List<AlbumPhotosByPage> findPhotoAlbumPageByAlbum_Id(Long albumId);
}
