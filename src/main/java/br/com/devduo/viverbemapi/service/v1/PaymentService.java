package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.PaymentController;
import br.com.devduo.viverbemapi.dtos.DashboardCardSummaryDTO;
import br.com.devduo.viverbemapi.dtos.PaymentPendingDTO;
import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.dtos.PaymentSummaryDTO;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.PaymentType;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.strategy.NewPaymentValidationStrategy;
import br.com.devduo.viverbemapi.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static br.com.devduo.viverbemapi.utils.PropertiesUtil.getNullPropertyNames;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository repository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PagedResourcesAssembler<Payment> assembler;
    @Autowired
    private ContractService contractService;
    @Autowired
    private List<NewPaymentValidationStrategy> newPaymentValidationStrategies;

    public Payment findById(Long id) {
        if (id == null)
            throw new BadRequestException("Payment ID cannot be null");

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
    }

    public PagedModel<EntityModel<Payment>> findAll(
            Pageable pageable, PaymentStatus status,
            YearMonth competency, PaymentType paymentType,
            Long tenantId
    ) {
        List<Payment> payments = repository.findAll(pageable, status, competency, paymentType, tenantId);

        PageImpl<Payment> paymentPage = new PageImpl<>(payments);

        Link link = linkTo(methodOn(PaymentController.class)
                .findAll(pageable, status, competency, paymentType, tenantId))
                .withSelfRel();

        return assembler.toModel(paymentPage, link);
    }

    @Transactional
    public String save(PaymentRequestDTO dto) {
        newPaymentValidationStrategies.forEach(strategy -> strategy.execute(dto));

        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Tenant's ID"));

        Contract contract = tenant.getContract();
        if (dto.getPaymentValue() < contract.getPrice()) {
            throw new BadRequestException("Payment value is lesser than Contract price");
        }

        int monthsLeftToPay = contractService.monthsLeftToPay(contract.getUuid());

        if (monthsLeftToPay == 0) {
            throw new BadRequestException("All payments linked to the %s contract have been debited".formatted(contract.getUuid()));
        }

        return processPayment(dto, tenant);
    }

    public String processPayment(PaymentRequestDTO dto, Tenant tenant) {
        Payment payment = repository.findByCompetencyAndTenantId(dto.getCompetency(), tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment for competency %s not found".formatted(dto.getCompetency())
                ));

        if (payment.getPaymentStatus() == PaymentStatus.PAID)
            return "The payment for competency %s has already been debited".formatted(payment.getCompetency());

        payment.setPaymentType(dto.getPaymentType());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentStatus(dto.getPaymentStatus());

        repository.save(payment);

        String formattedMonths = DateUtils.listLocalDateToString(List.of(payment.getCompetency()));
        return String.format("Payment for the month %s has been successfully registered", formattedMonths);
    }

    public Payment update(Long id, PaymentRequestDTO payment) {
        Payment paymentToUpdate = findById(id);
        BeanUtils.copyProperties(payment, paymentToUpdate, getNullPropertyNames(payment));
        return repository.save(paymentToUpdate);
    }

    public List<Payment> findPaymentsByTenant(Long tenantId) {
        if (tenantId == null)
            throw new BadRequestException("Tenant ID cannot be null");
        return repository.findByTenantId(tenantId);
    }

    public List<Payment> findByYearMonth(YearMonth yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        return repository.findByPaymentDateBetween(firstDayOfMonth, lastDayOfMonth);
    }

    public List<PaymentSummaryDTO> getSummaryPayments(LocalDate startDate, LocalDate endDate) {
        return repository.getSummaryPayments(startDate, endDate);
    }

    public List<PaymentPendingDTO> getPaymentsPending(LocalDate startDate, LocalDate endDate) {
        return repository.getPendingSummary(startDate, endDate);
    }

    public DashboardCardSummaryDTO getDashboardCardSummary(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        List<Tenant> tenants = tenantRepository.findAll(pageable, null, null, true);
        PagedModel<EntityModel<Contract>> contracts = contractService.findAll(pageable, true, null);

        double sumPaymentsPaid = getSummaryPayments(startDate, endDate).stream()
                .mapToDouble(PaymentSummaryDTO::getTotal)
                .sum();

        double sumPaymentsPending = getPaymentsPending(startDate, endDate).stream()
                .mapToDouble(PaymentPendingDTO::getTotalPending)
                .sum();

        return DashboardCardSummaryDTO.builder()
                .totalActiveContracts(contracts.getContent().size())
                .totalActiveTenants(tenants.size())
                .totalReceivedThisMonth(sumPaymentsPaid)
                .totalPendingThisMonth(sumPaymentsPending)
                .build();
    }
}
