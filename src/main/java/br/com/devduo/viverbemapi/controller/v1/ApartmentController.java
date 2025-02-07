package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Apartment>>> findAll(
            @ParameterObject Pageable pageable,
            @RequestParam(value = "ap_status", required = false) StatusApart statusApart
    ) {
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
