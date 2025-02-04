package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    @Query("SELECT c FROM Contract c WHERE c.tenant.id = :tenantId")
    Page<Contract> findAllByTenantIs(Pageable pageable, Long tenantId);

    Contract findContractByTenantId(Long tenantId);
}
