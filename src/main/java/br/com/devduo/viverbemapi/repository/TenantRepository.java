package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByName(String name);
}
