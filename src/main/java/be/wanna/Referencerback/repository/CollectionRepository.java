package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<UserCollection, Long> {
}
