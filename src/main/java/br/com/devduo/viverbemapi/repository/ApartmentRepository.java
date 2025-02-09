package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByNumberAp(Long numberAp);
}
