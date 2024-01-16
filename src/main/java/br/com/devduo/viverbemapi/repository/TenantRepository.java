package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenants, Long> {
    Tenants findByName(String name);
}
