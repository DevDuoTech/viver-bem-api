package br.com.devduo.viverbemapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSummaryDTO {
    private YearMonth competency;
    private Double total;
}
