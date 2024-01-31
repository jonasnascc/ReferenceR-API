package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.user.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}
