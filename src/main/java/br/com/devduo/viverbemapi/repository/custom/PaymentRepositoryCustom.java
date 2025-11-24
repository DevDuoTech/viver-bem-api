package br.com.devduo.viverbemapi.repository.custom;

import br.com.devduo.viverbemapi.dtos.PaymentPendingDTO;
import br.com.devduo.viverbemapi.dtos.PaymentSummaryDTO;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.PaymentType;
import br.com.devduo.viverbemapi.models.Payment;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface PaymentRepositoryCustom {
    List<Payment> findAll(
            Pageable pageable, PaymentStatus status,
            YearMonth competency, PaymentType paymentType,
            Long tenantId
    );

    List<PaymentSummaryDTO> getSummaryPayments(LocalDate startDate, LocalDate endDate);

     List<PaymentPendingDTO> getPendingSummary(LocalDate startDate, LocalDate endDate);
}
