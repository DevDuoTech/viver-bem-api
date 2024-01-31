package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByName(String name);

    @Query("SELECT t FROM Tenant t WHERE t.cpf = :cpf")
    Optional<Tenant> findByCPF(@Param("cpf") String cpf);
}
