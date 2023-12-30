package be.wanna.Referencerback.repository;


import be.wanna.Referencerback.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
}
