package br.com.devduo.viverbemapi.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Builder
@Data
public class TenantsRequestDTO {
    @Positive
    private Long id;
    @NotEmpty
    private String name;
    @CPF
    @NotEmpty
    private String cpf;
    @NotEmpty
    private String phone;
}
