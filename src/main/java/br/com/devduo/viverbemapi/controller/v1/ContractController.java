package br.com.devduo.viverbemapi.controller.v1;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.devduo.viverbemapi.dtos.ContractRequestSaveDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.service.v1.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contract", description = "Endpoint to Managing Contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @Operation(
            summary = "Finds all Contract",
            description = "Finds all Contract",
            tags = {"Contract"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Contract.class))
                            )
                            }),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Contract>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "startDate"));
        return ResponseEntity.ok(contractService.findAll(pageable));
    }

    @Operation(
            summary = "Finds a Contract",
            description = "Finds a Contract by UUID",
            tags = {"Contract"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Contract.class))
                            )
                            }),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<Contract> findByUuid(@PathVariable(value = "uuid") UUID uuid) {
        return ResponseEntity.ok(contractService.findByUuid(uuid));
    }

    @Operation(
            summary = "Adds a new Contract",
            description = "Adds a new Contract",
            tags = {"Contract"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<String> save(
            @RequestBody @Valid ContractRequestSaveDTO contractRequestDTO,
            @RequestParam(value = "num_ap") Long numAp
    ) {
        return new ResponseEntity<>(contractService.save(contractRequestDTO, numAp), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Updates a Contract",
            description = "Updates a Contract",
            tags = {"Contract"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ContractRequestUpdateDTO dto) {
        contractService.update(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
