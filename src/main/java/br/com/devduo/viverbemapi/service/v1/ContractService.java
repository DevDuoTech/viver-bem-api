package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.ContractController;
import br.com.devduo.viverbemapi.dtos.*;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;
import br.com.devduo.viverbemapi.repository.ContractRepository;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import br.com.devduo.viverbemapi.repository.TenantRepository;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PagedResourcesAssembler<Contract> assembler;
    @Autowired
    private PaymentService paymentService;

    public PagedModel<EntityModel<Contract>> findAll(Pageable pageable, Long tenantId) {
        Page<Contract> contractPage = contractRepository.findAllByTenantIs(pageable, tenantId);

        Link link = linkTo(methodOn(ContractController.class)
                .findAll(pageable, tenantId))
                .withSelfRel();

        return assembler.toModel(contractPage, link);
    }

    public Contract findByUuid(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("Contract UUID cannot be null");
        return contractRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for this UUID"));
    }

    @Transactional
    public String save(ContractRequestDTO dto, Long tenantId, Long numberAp) {
        if (dto == null)
            throw new BadRequestException("ContractDTO cannot be null");
        if (numberAp == null)
            throw new BadRequestException("Apartment number cannot be null");
        if (tenantId == null)
            throw new BadRequestException("Tenant ID cannot be null");

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found for this ID"));
        Apartment apartment = apartmentRepository.findByNumberAp(numberAp)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found for this ID"));

        if (apartment.getStatus().equals(StatusApart.OCCUPIED)) {
            throw new BadRequestException("Apartment %d is currently occupied".formatted(apartment.getNumberAp()));
        }

        apartment.setStatus(StatusApart.OCCUPIED);

        Contract contract = Contract.builder()
                .apartment(apartment)
                .tenant(tenant)
                .isActive(true)
                .build();
        BeanUtils.copyProperties(dto, contract);

        tenant.setContract(contract);

        tenantRepository.save(tenant);
        contractRepository.save(contract);

        int period = DateUtils.getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
        if (contract.getHasGuarantee())
            period++;

        for (int i = 0; i < period; i++) {
            Payment paymentBase = Payment.builder()
                    .tenant(tenant)
                    .value(contract.getPrice())
                    .paymentStatus(PaymentStatus.PAYABLE)
                    .competency(DateUtils.formatCompetency(contract.getDueDate(), contract.getStartDate()).plusMonths(i))
                    .build();

            paymentRepository.save(paymentBase);
        }

        return "Contract saved successfully";
    }

    @Transactional
    public void update(ContractRequestUpdateDTO dto) {
        if (dto == null)
            throw new BadRequestException("ContractRequestDTO cannot be null");

        ContractRequestDTO contractRequestDTO = dto.getContractRequestDTO();
        Contract contract = findByUuid(dto.getUuid());

        BeanUtils.copyProperties(contractRequestDTO, contract);

        contractRepository.save(contract);
    }

    public int monthsLeftToPay(UUID uuid) {
        Contract contract = findByUuid(uuid);
        List<Payment> paymentList = paymentRepository.findPaymentsByContractUuid(uuid);

        long durationOfStay = DateUtils.getMonthsBetweenDates(
                contract.getStartDate(),
                contract.getEndDate()
        );
        return (int) (durationOfStay - paymentList.size());
    }
}
