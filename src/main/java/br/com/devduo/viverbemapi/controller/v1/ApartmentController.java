package br.com.devduo.viverbemapi.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/api/v1/apartments")
@Tag(name = "Apartment", description = "Endpoint to Managing Apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

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

    @GetMapping(path = "/{ap_num}")
    public ResponseEntity<Apartment> findByNumberAp(@PathVariable(value = "ap_num") Long apNum) {
        return ResponseEntity.ok(apartmentService.findByNumberAp(apNum));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ApartmentsRequestDTO apartment) {
        apartmentService.update(apartment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{ap_num}")
    public ResponseEntity<Void> updateStatus(@PathVariable(value = "ap_num") Long apNum) {
        apartmentService.updateStatus(apNum);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
