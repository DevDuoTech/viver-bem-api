package br.com.devduo.viverbemapi.controller.v1;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant", description = "Endpoint to Managing Tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tenant>>> findAll(
            @ParameterObject Pageable pageable,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy_MM") YearMonth yearMonth,
            @RequestParam(value = "is_active", defaultValue = "true") Boolean isActive
    ) {
        return ResponseEntity.ok(tenantService.findAll(pageable, name, yearMonth, isActive));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @GetMapping("/by-cpf")
    public ResponseEntity<Tenant> findByCPF(@RequestParam(value = "cpf") String cpf) {
        return ResponseEntity.ok(tenantService.findByCPF(cpf));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Tenant> save(@RequestBody @Valid TenantsRequestDTO dto) {
        return new ResponseEntity<>(tenantService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid TenantsRequestDTO dto) {
        tenantService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
        tenantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
