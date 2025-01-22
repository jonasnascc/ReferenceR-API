package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.collections.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<UserCollection, Long> {

    List<UserCollection> findByUser(User user);

    UserCollection findByUserAndId(User user, Long id);

    @Query("SELECT p FROM UserCollection col JOIN col.photos p WHERE col.id = :collectionId ORDER BY p.savedDate DESC")
    Page<Photo> listPhotosByCollectionId_OrderBySavedDateDesc( Long collectionId, Pageable pageable);
}
