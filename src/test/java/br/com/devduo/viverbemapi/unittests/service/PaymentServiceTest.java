package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.models.Apartment;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService service;
    @Mock
    private PaymentRepository repository;
    @Mock
    private PagedResourcesAssembler<Payment> assembler;

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

    @Test
    @DisplayName("Finds all Payments and returns a PagedModel of the Payments successfully")
    public void testFindAllSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);

        List<Payment> paymentsMockList = PaymentMocks.paymentsMockList();
        Page<Payment> page = new PageImpl<>(paymentsMockList);

        when(repository.findAll(pageable)).thenReturn(page);

        EntityModel<Payment> entityModel = EntityModel.of(paymentsMockList.get(0));
        List<EntityModel<Payment>> entityModelList = Arrays.asList(entityModel);
        PagedModel<EntityModel<Payment>> expectedPagedModel = PagedModel.of(entityModelList,
                new PagedModel.PageMetadata(entityModelList.size(), 0, 2));

        Mockito.when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expectedPagedModel);

        PagedModel<EntityModel<Payment>> result = service.findAll(pageable);

        EntityModel<Payment> paymentEntityModel = result.getContent().stream().findFirst().get();
        Payment resultFirstPayment = paymentEntityModel.getContent();

        assertEquals(expectedPagedModel, result);

        assertNotNull(resultFirstPayment);
        assertNotNull(resultFirstPayment.getId());

        assertEquals(paymentsMockList.get(0).getId(), resultFirstPayment.getId());
        assertEquals(paymentsMockList.get(0).getPaymentDate(), resultFirstPayment.getPaymentDate());
        assertEquals(paymentsMockList.get(0).getPaymentStatus(), resultFirstPayment.getPaymentStatus());

    }
}