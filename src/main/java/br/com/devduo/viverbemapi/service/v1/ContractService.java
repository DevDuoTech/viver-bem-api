package br.com.devduo.viverbemapi.service.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

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

    public Contract findByUuid(UUID uuid) {
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
                .birthLocal(tenantDto.getBirthState())
                .build();

        Contract contract = Contract.builder()
                .startDate(contractRequestDTO.getStartDate())
                .endDate(contractRequestDTO.getEndDate())
                .price(contractRequestDTO.getPrice())
                .description(contractRequestDTO.getDescription())
                .apartment(apartment)
                .build();

        tenantService.save(tenant);
        contractRepository.save(contract);

        return "Contract saved successfully";
    }

    public void update(ContractRequestUpdateDTO dto) {
        if(dto == null)
            throw new BadRequestException("ContractRequestDTO cannot be null");

        ContractRequestDTO contractRequestDTO = dto.getContractRequestDTO();
        Contract contract = findByUuid(dto.getUuid());

        contract.setStartDate(contractRequestDTO.getStartDate());
        contract.setEndDate(contractRequestDTO.getEndDate());
        contract.setPrice(contractRequestDTO.getPrice());
        contract.setDescription(contractRequestDTO.getDescription());

        contractRepository.save(contract);
    }
}
