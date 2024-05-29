package br.com.devduo.viverbemapi.strategy;

import br.com.devduo.viverbemapi.dtos.PaymentRequestDTO;

public interface NewPaymentValidationStrategy {
    void execute(PaymentRequestDTO request);
}
