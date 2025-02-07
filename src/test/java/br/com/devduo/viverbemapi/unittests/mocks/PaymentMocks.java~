package br.com.devduo.viverbemapi.unittests.mocks;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.PaymentType;
import br.com.devduo.viverbemapi.models.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentMocks {
    public static Payment payablePaymentMock() {
        return Payment.builder()
                .id(1L)
                .price(BigDecimal.valueOf(500))
                .paymentType(PaymentType.CASH)
                .paymentStatus(PaymentStatus.PAYABLE)
                .paymentDate(LocalDate.of(2024, 3, 5))
                .tenantId(TenantMocks.mockActiveTenant().getId())
                .competency(LocalDate.of(2024,2,5))
                .build();
    }

    public static Payment paidPaymentMock() {
        return Payment.builder()
                .id(1L)
                .price(BigDecimal.valueOf(500))
                .paymentType(PaymentType.CASH)
                .paymentStatus(PaymentStatus.PAID)
                .paymentDate(LocalDate.of(2024, 3, 5))
                .tenantId(TenantMocks.mockActiveTenant().getId())
                .competency(LocalDate.of(2024,1,5))
                .build();
    }

    public static List<Payment> paymentsMockList() {
        return List.of(payablePaymentMock(), paidPaymentMock());
    }

    public static PaymentRequestDTO paidPaymentRequestDTO() {
        return PaymentRequestDTO.builder()
                .paymentValue(BigDecimal.valueOf(500))
                .paymentStatus(PaymentStatus.PAID)
                .paymentType(PaymentType.CASH)
                .paymentDate(LocalDate.of(2024, 3, 5))
                .tenantId(TenantMocks.mockActiveTenant().getId())
                .competency(LocalDate.of(2024,1,5))
                .build();
    }
    public static PaymentRequestDTO payablePaymentRequestDTO() {
        return PaymentRequestDTO.builder()
                .paymentValue(BigDecimal.valueOf(500))
                .paymentStatus(PaymentStatus.PAYABLE)
                .paymentType(PaymentType.CASH)
                .paymentDate(LocalDate.of(2024, 3, 5))
                .tenantId(TenantMocks.mockActiveTenant().getId())
                .competency(LocalDate.of(2024,2,5))
                .build();
    }
}
