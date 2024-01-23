package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<Apartment> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(apartmentService.findById(id));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ApartmentsRequestDTO apartment) {
        apartmentService.update(apartment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
