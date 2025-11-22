package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant", description = "Endpoint to Managing Tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<Tenant>>> findAll(
            @ParameterObject Pageable pageable,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy_MM") YearMonth yearMonth,
            @RequestParam(value = "is_active", defaultValue = "true") Boolean isActive,
            @RequestParam(value = "has_contract_active", required = false) Boolean hasContract
    ) {
        return ResponseEntity.ok(tenantService.findAll(pageable, name, yearMonth, isActive, hasContract));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'FIND_TENANT_BY_ID')")
    public ResponseEntity<Tenant> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @GetMapping("/by-cpf")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Tenant> findByCPF(@RequestParam(value = "cpf") String cpf) {
        return ResponseEntity.ok(tenantService.findByCPF(cpf));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Tenant> save(@RequestBody @Valid TenantsRequestDTO dto) {
        return new ResponseEntity<>(tenantService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#dto, 'UPDATE_TENANT')")
    public ResponseEntity<Void> update(@RequestBody @Valid TenantsRequestDTO dto) {
        tenantService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
        tenantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
