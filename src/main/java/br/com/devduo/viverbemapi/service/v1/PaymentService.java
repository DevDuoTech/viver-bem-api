package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.PaymentController;
import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

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

    //    TODO make this method transactional and coverage situations if paymentValue lower or greater than contract price
    public Payment save(PaymentRequestDTO dto) {
        if (dto == null)
            throw new BadRequestException("PaymentRequestDTO cannot be null");
        if (dto.getPaymentValue() == null)
            throw new BadRequestException("PaymentValue cannot be null");

        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Tenant's ID"));

        Payment payment = new Payment();
        BeanUtils.copyProperties(dto, payment);

        payment.setTenant(tenant);

        return repository.save(payment);
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
