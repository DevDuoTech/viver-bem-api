package br.com.devduo.viverbemapi.strategy.impl.payment;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.strategy.NewPaymentValidationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LesserPaymentValueValidation implements NewPaymentValidationStrategy {
    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public void execute(PaymentRequestDTO request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Tenant's ID"));
        Contract contract = tenant.getContract();
        if (request.getPaymentValue().compareTo(contract.getPrice()) < 0) {
            throw new BadRequestException("Payment value is lesser than Contract price");
        }
    }
}
