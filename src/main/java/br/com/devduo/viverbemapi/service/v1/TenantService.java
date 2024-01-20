package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.TenantController;
import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.TenantRepository;
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
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PagedResourcesAssembler<Tenant> assembler;

    public PagedModel<EntityModel<Tenant>> findAll(Pageable pageable) {
        Page<Tenant> tenantsPage = tenantRepository.findAll(pageable);

        Link link = linkTo(methodOn(TenantController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();

        return assembler.toModel(tenantsPage, link);
    }

    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
    }

    public Tenant save(Tenant tenant){
        return tenantRepository.save(tenant);
    }

    public void update(TenantsRequestDTO dto){
        Tenant tenantToUpdate = findById(dto.getId());

        tenantToUpdate.setName(dto.getName());
        tenantToUpdate.setCpf(dto.getCpf());
        tenantToUpdate.setPhone(dto.getPhone());

        tenantRepository.save(tenantToUpdate);
    }

    public void delete(Long id){
        Tenant tenant = findById(id);
        tenantRepository.delete(tenant);
    }
}
