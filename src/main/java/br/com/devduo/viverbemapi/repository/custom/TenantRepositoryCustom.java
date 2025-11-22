package br.com.devduo.viverbemapi.repository.custom;

import br.com.devduo.viverbemapi.models.Tenant;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TenantRepositoryCustom {
    List<Tenant> findAll(Pageable pageable, String name, Boolean isActive, Boolean hasContractActive);
}
