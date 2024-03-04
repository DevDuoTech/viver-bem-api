package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.enums.RoleEnum;
import br.com.devduo.viverbemapi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByDescription(RoleEnum roleEnum);
}
