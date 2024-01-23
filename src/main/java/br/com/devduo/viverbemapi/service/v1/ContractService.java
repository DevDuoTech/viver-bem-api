package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.ContractController;
import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private PagedResourcesAssembler<Contract> assembler;

    public PagedModel<EntityModel<Contract>> findAll(Pageable pageable) {
        Page<Contract> contractPage = contractRepository.findAll(pageable);

        Link link = linkTo(methodOn(ContractController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();

        return assembler.toModel(contractPage, link);
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found for this ID"));
    }

    public String save(ContractRequestDTO contractRequestDto, Long numberAp) {
        if (contractRequestDto == null)
            throw new BadRequestException("ContractDTO cannot be null");
        if (contractRequestDto.getTenantsRequestDTO() == null)
            throw new BadRequestException("TenantDTO cannot be null");
        if (numberAp == null)
            throw new BadRequestException("Apartment ID cannot be null");

        TenantsRequestDTO tenantDto = contractRequestDto.getTenantsRequestDTO();
        Apartment apartment = apartmentService.findByNumberAp(numberAp);
        apartment.setStatus(StatusApart.OCCUPIED);

        Tenant tenant = Tenant.builder()
                .name(tenantDto.getName())
                .cpf(tenantDto.getCpf())
                .phone(tenantDto.getPhone())
                .rg(tenantDto.getRg())
                .birthDate(tenantDto.getBirthDate())
                .birthState(tenantDto.getBirthState())
                .apartment(apartment)
                .build();

        Contract contract = Contract.builder()
                .startDate(contractRequestDto.getStartDate())
                .endDate(contractRequestDto.getEndDate())
                .price(contractRequestDto.getPrice())
                .description(contractRequestDto.getDescription())
                .apartment(apartment)
                .build();

        tenantService.save(tenant);
        contractRepository.save(contract);

        return "Contract saved successfully";
    }
}
