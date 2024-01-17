package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tenant>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ){
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        return ResponseEntity.ok(tenantService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> findById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Tenant> save(@RequestBody @Valid Tenant tenant){
        return new ResponseEntity<>(tenantService.save(tenant), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid TenantsRequestDTO dto){
        tenantService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id){
        tenantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
