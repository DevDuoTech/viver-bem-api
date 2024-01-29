package br.com.devduo.viverbemapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestDTO {
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "{field.startDate.required}")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "{field.endDate.required}")
    private LocalDate endDate;
    @NotNull(message = "{field.price.required}")
    @Positive(message = "{field.price.invalid}")
    private BigDecimal price;
    private String description;

    @Valid
    @NotNull(message = "{field.tenant.required}")
    private TenantsRequestDTO tenantsRequestDTO;
}
