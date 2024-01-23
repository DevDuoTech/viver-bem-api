package br.com.devduo.viverbemapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantsRequestDTO {
    @NotEmpty
    private String name;
    @CPF
    @NotEmpty
    private String cpf;
    @NotEmpty
    private String phone;
    @NotEmpty
    @Column(length = 9)
    private String rg;
    @NotNull
    @Past
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @NotEmpty
    @Column(name = "birth_state")
    private String birthState;
}
