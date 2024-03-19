package br.com.devduo.viverbemapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
    @Min(1)
    @Max(31)
    private Integer dueDate;
    @NotNull
    private Boolean hasGuarantee;
    private String description;
}
