package br.com.devduo.viverbemapi.dtos;

import br.com.devduo.viverbemapi.enums.StatusApart;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApartmentsRequestDTO {
    @Positive
    private Long id;
    @NotBlank
    private String description;
    @Enumerated(EnumType.STRING)
    private StatusApart status;
}
