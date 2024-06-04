package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.PaymentController;
import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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

    public PagedModel<EntityModel<Payment>> findAll(Pageable pageable) {
        Page<Payment> paymentPage = repository.findAll(pageable);

        Link link = linkTo(methodOn(PaymentController.class)
                .findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "desc"
                ))
                .withSelfRel();

        return assembler.toModel(paymentPage, link);
    }

    @Transactional
    public String save(PaymentRequestDTO dto) {
        newPaymentValidationStrategies.forEach(strategy -> strategy.execute(dto));

        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Tenant's ID"));

        Contract contract = tenant.getContract();
        if (dto.getPaymentValue().compareTo(contract.getPrice()) < 0) {
            throw new BadRequestException("Payment value is lesser than Contract price");
        }

        int numberOfMonthsToPay = dto.getPaymentValue().divide(contract.getPrice()).intValue();
        int monthsLeftToPay = contractService.monthsLeftToPay(contract.getUuid());

        if (monthsLeftToPay == 0) {
            throw new BadRequestException("All payments linked to the %s contract have been debited".formatted(contract.getUuid()));
        }

        List<LocalDate> monthsPaid = processPayments(dto, tenant, numberOfMonthsToPay);

        String formattedMonths = DateUtils.listLocalDateToString(monthsPaid);
        return String.format("Payment for the months %s has been successfully registered", formattedMonths);
    }

    public List<LocalDate> processPayments(PaymentRequestDTO dto, Tenant tenant, int numberOfMonthsToPay) {
        List<LocalDate> monthsPaid = new ArrayList<>();
        LocalDate paymentDate = dto.getPaymentDate();

        List<Payment> paymentsPayable = tenant.getPayments()
                .stream()
                .filter(p -> Objects.equals(p.getPaymentStatus(), PaymentStatus.PAYABLE.toString()))
                .sorted(Comparator.comparing(Payment::getCompetency))
                .toList();

        int monthsLeftToPay = paymentsPayable.size();

        for (int i = 0; i < numberOfMonthsToPay && monthsLeftToPay > 0; i++) {
            if (i > 0) {
                paymentDate = paymentDate.plusMonths(1);
            }

            Payment payment = paymentsPayable.get(i);
            update(payment);

            monthsPaid.add(payment.getPaymentDate());

            monthsLeftToPay--;
        }

        return monthsPaid;
    }

    public Payment update(Payment payment) {
        Payment paymentToUpdate = findById(payment.getId());
        BeanUtils.copyProperties(payment, paymentToUpdate);
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
}
