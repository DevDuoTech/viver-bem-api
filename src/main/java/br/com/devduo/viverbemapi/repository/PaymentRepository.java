package br.com.devduo.viverbemapi.repository;

import br.com.devduo.viverbemapi.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenantId(Long id);

    @Query("SELECT p FROM Payment p " +
            "JOIN p.tenant t " +
            "JOIN t.contract c " +
            "WHERE c.uuid = :contractUuid")
    List<Payment> findPaymentsByContractUuid(@Param("contractUuid") UUID contractUuid);

    List<Payment> findByPaymentDateBetween(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth);
}
