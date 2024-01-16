package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Contracts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contracts, Long> {
}
