package br.com.devduo.viverbemapi.unittests.mocks;

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
                .tenant(TenantMocks.mockActiveTenant())
                .build();
    }

    public static Payment paidPaymentMock() {
        return Payment.builder()
                .id(1L)
                .price(BigDecimal.valueOf(500))
                .paymentType(PaymentType.CASH)
                .paymentStatus(PaymentStatus.PAID)
                .paymentDate(LocalDate.of(2024, 3, 5))
                .tenant(TenantMocks.mockDisableTenant())
                .build();
    }

    public static List<Payment> paymentsMockList() {
        return List.of(payablePaymentMock(), paidPaymentMock());
    }
}