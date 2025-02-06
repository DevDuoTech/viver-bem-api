package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.service.v1.ContractService;
import br.com.devduo.viverbemapi.service.v1.PaymentService;
import br.com.devduo.viverbemapi.strategy.NewPaymentValidationStrategy;
import br.com.devduo.viverbemapi.strategy.impl.payment.NonNullPaymentValidation;
import br.com.devduo.viverbemapi.strategy.impl.payment.NonNullPaymentValueValidation;
import br.com.devduo.viverbemapi.unittests.mocks.ContractMocks;
import br.com.devduo.viverbemapi.unittests.mocks.PaymentMocks;
import br.com.devduo.viverbemapi.unittests.mocks.TenantMocks;
import br.com.devduo.viverbemapi.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService service;
    @Mock
    private ContractService contractService;
    @Mock
    private PaymentRepository repository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private PagedResourcesAssembler<Payment> assembler;
    @Mock
    private NonNullPaymentValidation nonNullPaymentValidation;
    @Mock
    private NonNullPaymentValueValidation nonNullPaymentValueValidation;
    @Spy
    private List<NewPaymentValidationStrategy> newPaymentValidationStrategies = new ArrayList<>();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        newPaymentValidationStrategies.add(nonNullPaymentValidation);
        newPaymentValidationStrategies.add(nonNullPaymentValueValidation);
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
        assertEquals(expectedPayment.getTenantId(), resultPayment.getTenantId());
    }

    @Test
    @DisplayName("Finds all Payments and returns a PagedModel of the Payments successfully")
    public void testFindAllSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);

        List<Payment> paymentsMockList = PaymentMocks.paymentsMockList();
        Page<Payment> page = new PageImpl<>(paymentsMockList);

        when(repository.findAll(pageable)).thenReturn(page);

        EntityModel<Payment> entityModel = EntityModel.of(paymentsMockList.get(0));
        List<EntityModel<Payment>> entityModelList = List.of(entityModel);
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

    @Test
    @DisplayName("Find a Payment by your ID and returns a Payment successfully")
    public void testFindByIdSuccessfully() {
        Payment paymentMock = PaymentMocks.paidPaymentMock();

        when(repository.findById(paymentMock.getId())).thenReturn(Optional.of(paymentMock));

        Payment result = service.findById(paymentMock.getId());

        assertNotNull(result);
        assertNotNull(result.getId());

        assertEquals(paymentMock.getPaymentDate(), result.getPaymentDate());
        assertEquals(paymentMock.getPaymentStatus(), result.getPaymentStatus());
        assertEquals(paymentMock.getPaymentType(), result.getPaymentType());
        assertEquals(paymentMock.getValue(), result.getValue());
    }

    @Test
    @DisplayName("Find a Payment with null ID and throw a BadRequestException")
    public void testFindByIdAndThrowBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.findById(null));

        String expectedMessage = "Payment ID cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Find a Payment with unknown ID and throw a ResourceNotFoundException")
    public void testFindByIdAndThrowResourceNotFoundException() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(any(Long.class)));

        String expectedMessage = "No records found for this ID";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Creates a new Payment and saves it successfully")
    public void testSaveNewPaymentSuccessfully() {
        PaymentRequestDTO mockDto = PaymentMocks.payablePaymentRequestDTO();
        Tenant mockTenant = TenantMocks.mockActiveTenant();
        Contract mockContract = ContractMocks.mockContract();
        mockTenant.setContract(mockContract);
        Payment mockPayment = PaymentMocks.payablePaymentMock();

        when(tenantRepository.findById(mockDto.getTenantId())).thenReturn(Optional.of(mockTenant));
        when(contractService.monthsLeftToPay(mockContract.getUuid())).thenReturn(1);
        when(repository.findByCompetencyAndTenantId(mockDto.getCompetency(), mockTenant.getId()))
                .thenReturn(Optional.of(mockPayment));

        String result = service.save(mockDto);

        assertNotNull(result);
        assertTrue(result.contains("has been successfully registered"));
        verify(repository).save(mockPayment);
        verify(repository).findByCompetencyAndTenantId(mockDto.getCompetency(), mockTenant.getId());
        verify(tenantRepository).findById(mockDto.getTenantId());
        verify(contractService).monthsLeftToPay(mockContract.getUuid());
    }

    @Test
    @DisplayName("Tries to save new Payment with non-existent Tenant and throws a ResourceNotFoundException")
    public void testSaveWithoutTenantId() {
        PaymentRequestDTO mockDto = PaymentMocks.payablePaymentRequestDTO();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.save(mockDto));

        String expectedMessage = "No records found for this Tenant's ID";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to make a Payment without the payment being available and throws a BadRequestException")
    public void testSaveWithoutPaymentLeft() {
        PaymentRequestDTO mockDto = PaymentMocks.payablePaymentRequestDTO();
        Tenant mockTenant = TenantMocks.mockActiveTenant();
        Contract mockContract = ContractMocks.mockContract();
        mockTenant.setContract(mockContract);

        when(tenantRepository.findById(mockDto.getTenantId())).thenReturn(Optional.of(mockTenant));
        when(contractService.monthsLeftToPay(mockContract.getUuid())).thenReturn(0);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.save(mockDto));

        String expectedMessage = "All payments linked to the %s contract have been debited".formatted(mockContract.getUuid());
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to save a new Payment with value lower than contract price and throws a BandRequestException")
    public void testSaveWithLowerValue() {
        PaymentRequestDTO mockDto = PaymentMocks.payablePaymentRequestDTO();
        mockDto.setPaymentValue(BigDecimal.valueOf(150));

        Tenant mockTenant = TenantMocks.mockActiveTenant();
        Contract mockContract = ContractMocks.mockContract();
        mockTenant.setContract(mockContract);

        when(tenantRepository.findById(mockTenant.getId())).thenReturn(Optional.of(mockTenant));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.save(mockDto));

        String expectedMessage = "Payment value is lesser than Contract price";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to save a Payment with null dto and throws a BadRequestException")
    public void testSaveWithoutDtoParm() {
        doThrow(new BadRequestException("PaymentRequestDTO cannot be null"))
                .when(nonNullPaymentValidation).execute(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.save(null));

        String expectedMessage = "PaymentRequestDTO cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to save a Payment with null PaymentValue and throws a BadRequestException")
    public void testSaveWithoutPaymentValue() {
        PaymentRequestDTO paymentDtoMock = PaymentMocks.paidPaymentRequestDTO();
        paymentDtoMock.setPaymentValue(null);

        doThrow(new BadRequestException("PaymentValue cannot be null"))
                .when(nonNullPaymentValueValidation).execute(paymentDtoMock);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.save(paymentDtoMock));

        String expectedMessage = "PaymentValue cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Finds a list of Payments by Tenant ID successfully")
    public void testFindPaymentByTenant() {
        List<Payment> paymentsMockList = PaymentMocks.paymentsMockList();

        when(repository.findByTenantId(1L)).thenReturn(paymentsMockList);

        List<Payment> result = service.findPaymentsByTenant(1L);

        assertNotNull(result);
        assertEquals(paymentsMockList.size(), result.size());

        assertEquals(paymentsMockList.get(0).getId(), result.get(0).getId());
        assertEquals(paymentsMockList.get(0).getTenantId(), result.get(0).getTenantId());
        assertEquals(paymentsMockList.get(0).getPaymentDate(), result.get(0).getPaymentDate());
    }

    @Test
    @DisplayName("Tries to find a list of Payments with a null Tenant ID and throws a BadRequestException")
    public void testFindPaymentByTenantAndThrowsBadRequestException() {
        Exception exception = assertThrows(BadRequestException.class, () -> service.findPaymentsByTenant(null));

        String expectedMessage = "Tenant ID cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Process Payment successfully")
    public void testProcessPayment() {
        PaymentRequestDTO mockPaymentDto = PaymentMocks.payablePaymentRequestDTO();

        Tenant mockActiveTenant = TenantMocks.mockActiveTenant();
        Contract mockedContract = ContractMocks.mockContract();
        mockedContract.setTenant(mockActiveTenant);

        when(repository.findByCompetencyAndTenantId(mockPaymentDto.getCompetency(), mockActiveTenant.getId()))
                .thenReturn(Optional.of(PaymentMocks.payablePaymentMock()));
        when(repository.save(any(Payment.class))).thenReturn(PaymentMocks.payablePaymentMock());

        String result = service.processPayment(mockPaymentDto, mockActiveTenant);

        String formattedMonths = DateUtils.listLocalDateToString(List.of(mockPaymentDto.getCompetency()));
        String expectedMessage = "Payment for the month %s has been successfully registered".formatted(formattedMonths);

        assertEquals(expectedMessage, result);

        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Try to process a payment that has already been debited and cancel the processing")
    public void testProcessPaymentAlreadyPaid() {
        PaymentRequestDTO mockPaymentDto = PaymentMocks.paidPaymentRequestDTO();

        Tenant mockActiveTenant = TenantMocks.mockActiveTenant();
        Contract mockedContract = ContractMocks.mockContract();
        mockedContract.setTenant(mockActiveTenant);

        when(repository.findByCompetencyAndTenantId(mockPaymentDto.getCompetency(), mockActiveTenant.getId()))
                .thenReturn(Optional.of(PaymentMocks.paidPaymentMock()));

        String result = service.processPayment(mockPaymentDto, mockActiveTenant);
        String expectedMessage = "The payment for competency %s has already been debited".formatted(mockPaymentDto.getCompetency());

        assertEquals(expectedMessage, result);
    }

    @Test
    @DisplayName("Try to process a non-existent payment and throw a ResourceNotFoundException")
    public void testProcessAndThrowNotFoundException() {
        PaymentRequestDTO mockPaymentDto = PaymentMocks.paidPaymentRequestDTO();
        Tenant mockActiveTenant = TenantMocks.mockActiveTenant();
        Contract mockedContract = ContractMocks.mockContract();
        mockedContract.setTenant(mockActiveTenant);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> service.processPayment(mockPaymentDto, mockActiveTenant));

        String expectedMessage = "Payment for competency %s not found".formatted(mockPaymentDto.getCompetency());
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Updates a existent Payment successfully")
    public void testUpdateSuccessfully() {
        Payment existingPayablePayment = PaymentMocks.payablePaymentMock();
        existingPayablePayment.setId(1L);
        Payment existingPayablePaymentCopy = PaymentMocks.payablePaymentMock();
        BeanUtils.copyProperties(existingPayablePayment, existingPayablePaymentCopy);

        Payment paidedPaymentMock = PaymentMocks.paidPaymentMock();
        paidedPaymentMock.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existingPayablePaymentCopy));
        when(repository.save(existingPayablePaymentCopy)).thenReturn(paidedPaymentMock);

        Payment result = service.update(paidedPaymentMock);

        assertNotNull(result);
        assertEquals(paidedPaymentMock.getId(), result.getId());

        assertEquals(paidedPaymentMock.getPaymentStatus(), result.getPaymentStatus());
        assertNotEquals(existingPayablePayment.getPaymentStatus(), result.getPaymentStatus());
    }
}
