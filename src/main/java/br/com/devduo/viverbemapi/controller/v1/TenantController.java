package br.com.devduo.viverbemapi.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant", description = "Endpoint to Managing Tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @Operation(
            summary = "Finds all Tenant",
            description = "Finds all Tenant",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Tenant.class))
                            )
                            }),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tenant>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy_MM") YearMonth yearMonth,
            @RequestParam(value = "is_active", defaultValue = "true") Boolean isActive
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "payments.paymentDate"));
        return ResponseEntity.ok(tenantService.findAll(pageable, name, yearMonth, isActive));
    }

    @Operation(
            summary = "Finds a Tenant",
            description = "Finds a Tenant by ID",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Tenant.class))
                            )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @Operation(
            summary = "Finds a Tenant",
            description = "Finds a Tenant by CPF",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Tenant.class))
                            )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping("/by-cpf")
    public ResponseEntity<Tenant> findByCPF(@RequestParam(value = "cpf") String cpf) {
        return ResponseEntity.ok(tenantService.findByCPF(cpf));
    }

    @Operation(
            summary = "Adds a new Tenant",
            description = "Adds a new Tenant",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = Tenant.class))
                    ),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<Tenant> save(@RequestBody @Valid Tenant tenant) {
        return new ResponseEntity<>(tenantService.save(tenant), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Updates a Tenant",
            description = "Updates a Tenant",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid TenantsRequestDTO dto) {
        tenantService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Deletes a Tenant",
            description = "Deletes a Tenant",
            tags = {"Tenant"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
        tenantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
