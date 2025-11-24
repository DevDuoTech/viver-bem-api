package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.service.v1.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contract", description = "Endpoint to Managing Contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#tenantId, 'FIND_ALL_CONTRACTS')")
    public ResponseEntity<PagedModel<EntityModel<Contract>>> findAll(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long tenantId
    ) {
        return ResponseEntity.ok(contractService.findAll(pageable, isActive, tenantId));
    }

    @GetMapping("/{uuid}")
    @PostAuthorize("hasRole('ROLE_ADMIN') or hasPermission(returnObject, 'GET_CONTRACT_BY_UUID')")
    public ResponseEntity<Contract> findByUuid(@PathVariable(value = "uuid") UUID uuid) {
        return ResponseEntity.ok(contractService.findByUuid(uuid));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> save(
            @RequestBody @Valid ContractRequestDTO contractRequestDTO,
            @RequestHeader(value = "tenantId") Long tenantId,
            @RequestHeader(value = "num_ap") Long numAp
    ) {
        return new ResponseEntity<>(contractService.save(contractRequestDTO, tenantId, numAp), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> update(@RequestBody @Valid ContractRequestUpdateDTO dto) {
        contractService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
