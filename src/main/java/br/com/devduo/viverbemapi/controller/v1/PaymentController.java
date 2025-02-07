package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.service.v1.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Endpoint to Managing Payments")
public class PaymentController {
    @Autowired
    private PaymentService service;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Payment>>> findAll(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody PaymentRequestDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Payment>> findByPaymentDate(
            @PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy_MM") YearMonth date
    ) {
        return ResponseEntity.ok(service.findByYearMonth(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
