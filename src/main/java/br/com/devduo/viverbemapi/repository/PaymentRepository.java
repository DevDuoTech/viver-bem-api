package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
