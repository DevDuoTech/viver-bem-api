package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/apartments")
@Tag(name = "Apartment", description = "Endpoint to Managing Apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @Operation(
            summary = "Finds all Apartment",
            description = "Finds all Apartment",
            tags = {"Apartment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Apartment.class))
                            )
                            }),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Apartment>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "ap_status", required = false) StatusApart statusApart
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "numberAp"));
        return ResponseEntity.ok(apartmentService.findAll(pageable, statusApart));
    }

    @Operation(
            summary = "Finds a Apartment",
            description = "Finds a Apartment",
            tags = {"Apartment"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Apartment.class))
                            )
                            }),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping(path = "/{ap_num}")
    public ResponseEntity<Apartment> findByNumberAp(@PathVariable(value = "ap_num") Long apNum) {
        return ResponseEntity.ok(apartmentService.findByNumberAp(apNum));
    }

    @Operation(
            summary = "Updates a Apartment",
            description = "Updates a Apartment",
            tags = {"Apartment"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ApartmentsRequestDTO apartment) {
        apartmentService.update(apartment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Updates Apartment Status",
            description = "Updates Apartment Status",
            tags = {"Apartment"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PatchMapping(path = "/{ap_num}")
    public ResponseEntity<Void> updateStatus(@PathVariable(value = "ap_num") Long apNum) {
        apartmentService.updateStatus(apNum);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
