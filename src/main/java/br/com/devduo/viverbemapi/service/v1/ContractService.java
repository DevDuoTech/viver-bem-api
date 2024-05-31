package br.com.devduo.viverbemapi.service.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import br.com.devduo.viverbemapi.models.Payment;
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

import br.com.devduo.viverbemapi.controller.v1.ContractController;
import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestSaveDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.ContractRepository;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PagedResourcesAssembler<Contract> assembler;

    public PagedModel<EntityModel<Contract>> findAll(Pageable pageable) {
        Page<Contract> contractPage = contractRepository.findAll(pageable);

        Link link = linkTo(methodOn(ContractController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();

        return assembler.toModel(contractPage, link);
    }

    public Contract findByUuid(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("Contract UUID cannot be null");
        return contractRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for this UUID"));
    }

    public String save(ContractRequestSaveDTO dto, Long numberAp) {
        if (dto == null)
            throw new BadRequestException("ContractDTO cannot be null");
        if (numberAp == null)
            throw new BadRequestException("Apartment number cannot be null");

        ContractRequestDTO contractRequestDTO = dto.getContractRequestDTO();
        TenantsRequestDTO tenantDto = dto.getTenantsRequestDTO();
        Apartment apartment = apartmentService.findByNumberAp(numberAp);

        if (apartment.getStatus().equals(StatusApart.OCCUPIED))
            throw new BadRequestException("Apartment %d is currently occupied".formatted(apartment.getNumberAp()));

        apartment.setStatus(StatusApart.OCCUPIED);

        Tenant tenant = Tenant.builder()
                .name(tenantDto.getName())
                .cpf(tenantDto.getCpf())
                .phone(tenantDto.getPhone())
                .rg(tenantDto.getRg())
                .birthDate(tenantDto.getBirthDate())
                .birthLocal(tenantDto.getBirthLocal())
                .isActive(tenantDto.getIsActive())
                .build();

        Contract contract = Contract.builder()
                .startDate(contractRequestDTO.getStartDate())
                .endDate(contractRequestDTO.getEndDate())
                .dueDate(contractRequestDTO.getDueDate())
                .price(contractRequestDTO.getPrice())
                .description(contractRequestDTO.getDescription())
                .hasGuarantee(contractRequestDTO.getHasGuarantee())
                .apartment(apartment)
                .tenant(tenant)
                .build();

        tenant.setContract(contract);

        tenantRepository.save(tenant);
        contractRepository.save(contract);

        return "Contract saved successfully";
    }

    public void update(ContractRequestUpdateDTO dto) {
        if(dto == null)
            throw new BadRequestException("ContractRequestDTO cannot be null");

        ContractRequestDTO contractRequestDTO = dto.getContractRequestDTO();
        Contract contract = findByUuid(dto.getUuid());

        BeanUtils.copyProperties(contractRequestDTO, contract);

        contractRepository.save(contract);
    }
    
    public int monthsLeftToPay(UUID uuid){
        Contract contract = findByUuid(uuid);
        List<Payment> paymentList = paymentRepository.findPaymentsByContractUuid(uuid);

        long durationOfStay = ChronoUnit.MONTHS.between(contract.getStartDate(), contract.getEndDate()) + 1;
        return (int) (durationOfStay - paymentList.size());
    }
}
