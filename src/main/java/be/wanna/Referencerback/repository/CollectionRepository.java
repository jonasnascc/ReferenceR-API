package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.UserCollection;
import be.wanna.Referencerback.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<UserCollection, Long> {

    List<UserCollection> findByUser(User user);

    UserCollection findByUserAndId(User user, Long id);
}
