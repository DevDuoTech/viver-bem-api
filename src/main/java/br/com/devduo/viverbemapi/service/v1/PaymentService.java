package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.models.Payment;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository repository;
    @Autowired
    private TenantService tenantService;

    public Payment save(PaymentRequestDTO dto) {
        if(dto == null)
            throw new BadRequestException("PaymentRequestDTO cannot be null");

        Tenant tenant = tenantService.findById(dto.getTenantId());

        Payment payment = Payment.builder()
                .price(dto.getPrice())
                .paymentDate(LocalDate.now())
                .paymentType(dto.getPaymentType())
                .paymentStatus(dto.getPaymentStatus())
                .tenant(tenant)
                .build();

        return repository.save(payment);
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }
}
