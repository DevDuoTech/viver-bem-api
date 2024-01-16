package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Apartments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartments, Long> {
}
