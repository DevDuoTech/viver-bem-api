package br.com.devduo.viverbemapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPendingDTO {
    private Long tenantId;
    private String tenantName;
    private Double totalPending;
    private Long count;
}
