package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import br.com.devduo.viverbemapi.service.v1.PaymentService;
import br.com.devduo.viverbemapi.unittests.mocks.PaymentMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService service;
    @Mock
    private PaymentRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Find all payments for a month successfully")
    public void testFindPaymentsByYearMonthSuccessfully() {
        List<Payment> paymentsMockList = PaymentMocks.paymentsMockList();

        LocalDate firstDate = LocalDate.of(2024, 3, 1);
        LocalDate lastDate = LocalDate.of(2024, 3, 31);

        when(repository.findByPaymentDateBetween(firstDate, lastDate)).thenReturn(paymentsMockList);

        List<Payment> result = service.findByYearMonth(YearMonth.of(2024, 3));

        Payment expectedPayment = paymentsMockList.get(0);
        Payment resultPayment = result.get(0);

        assertNotNull(result);
        assertEquals(paymentsMockList.size(), result.size());

        assertEquals(expectedPayment.getPaymentDate(), resultPayment.getPaymentDate());
        assertEquals(expectedPayment.getPaymentStatus(), resultPayment.getPaymentStatus());
        assertEquals(expectedPayment.getTenant(), resultPayment.getTenant());
    }
}
