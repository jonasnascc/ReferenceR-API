package be.wanna.Referencerback.repository;

import be.wanna.Referencerback.entity.collections.CollectionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CollectionLogRepository extends JpaRepository<CollectionLog, Long> {
    Set<CollectionLog> findCollectionLogsByCollection_Id(Long id);

}
