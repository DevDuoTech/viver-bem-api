package br.com.devduo.viverbemapi.dtos;

import br.com.devduo.viverbemapi.enums.StatusApart;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApartmentsRequestDTO {
    @Positive
    private Long id;
    @NotEmpty
    private String description;
    @NotEmpty
    @Enumerated(EnumType.STRING)
    private StatusApart status;
}
