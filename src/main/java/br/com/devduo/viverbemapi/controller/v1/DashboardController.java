package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.DashboardCardSummaryDTO;
import br.com.devduo.viverbemapi.dtos.PaymentPendingDTO;
import br.com.devduo.viverbemapi.dtos.PaymentSummaryDTO;
import br.com.devduo.viverbemapi.service.v1.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoint to managing Dashboard")
public class DashboardController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/summary")
    public ResponseEntity<List<PaymentSummaryDTO>> summaryPayments(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
    ) {
        return new ResponseEntity<>(paymentService.getSummaryPayments(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/pending-summary")
    public ResponseEntity<List<PaymentPendingDTO>> findPaymentsPendingPerTenant(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentService.getPaymentsPending(startDate, endDate));
    }

    @GetMapping("/cards")
    public ResponseEntity<DashboardCardSummaryDTO> getDashboardCardSummary(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentService.getDashboardCardSummary(pageable, startDate, endDate));
    }
}
