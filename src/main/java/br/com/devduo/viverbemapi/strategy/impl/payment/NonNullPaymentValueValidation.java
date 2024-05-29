package br.com.devduo.viverbemapi.strategy.impl.payment;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.strategy.NewPaymentValidationStrategy;
import org.springframework.stereotype.Component;

@Component
public class NonNullPaymentValueValidation implements NewPaymentValidationStrategy {
    @Override
    public void execute(PaymentRequestDTO request) {
        if (request.getPaymentValue() == null)
            throw new BadRequestException("PaymentValue cannot be null");
    }
}
