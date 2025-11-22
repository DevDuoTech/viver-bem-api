package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.TenantController;
import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.devduo.viverbemapi.utils.PropertiesUtil.getNullPropertyNames;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PagedResourcesAssembler<Tenant> assembler;

    public PagedModel<EntityModel<Tenant>> findAll(
            Pageable pageable, String name,
            YearMonth yearMonth, Boolean isActive,
            Boolean hasContractActive) {
        List<Tenant> tenantList = tenantRepository.findAll(pageable, name, isActive, hasContractActive);

        Page<Tenant> tenantFiltered = new PageImpl<>(tenantList);

        Link link = linkTo(methodOn(TenantController.class)
                .findAll(
                        pageable,
                        name,
                        yearMonth,
                        isActive,
                        hasContractActive
                ))
                .withSelfRel();

        return assembler.toModel(tenantFiltered, link);
    }

    public Tenant findById(Long id) {
        if (id == null)
            throw new BadRequestException("Tenant ID cannot be null");
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
    }

    public Tenant findByCPF(String cpf) {
        if (cpf == null)
            throw new BadRequestException("Tenant CPF cannot be null");
        return tenantRepository.findByCPF(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this CPF"));
    }

    @Transactional
    public Tenant save(TenantsRequestDTO dto) {
        if (dto == null)
            throw new BadRequestException("Tenant cannot be null");

        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);

        return tenantRepository.save(tenant);
    }

    @Transactional
    public void update(TenantsRequestDTO dto) {
        if (dto == null)
            throw new BadRequestException("Tenant cannot be null");

        Tenant tenantToUpdate = tenantRepository.findByCPF(dto.getCpf())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this CPF"));

        BeanUtils.copyProperties(dto, tenantToUpdate, getNullPropertyNames(dto));

        tenantRepository.save(tenantToUpdate);
    }

    public void delete(Long id) {
        Tenant tenant = findById(id);
        tenantRepository.delete(tenant);
    }

}
