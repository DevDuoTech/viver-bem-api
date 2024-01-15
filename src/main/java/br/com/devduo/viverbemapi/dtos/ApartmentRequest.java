package br.com.devduo.viverbemapi.dtos;

import br.com.devduo.viverbemapi.enums.StatusApart;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ApartmentRequest {
    private Long id;
    private String description;
    @Enumerated(EnumType.STRING)
    private StatusApart status;
}
