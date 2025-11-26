package br.com.devduo.viverbemapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCardSummaryDTO {
    private Integer totalActiveTenants;
    private Integer totalActiveContracts;
    private Double totalReceivedThisMonth;
    private Double totalPendingThisMonth;
}
