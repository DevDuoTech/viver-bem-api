package br.com.devduo.viverbemapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantsRequestDTO {
    @NotEmpty(message = "{field.name.required}")
    private String name;
    @CPF(message = "{field.cpf.invalid}")
    @NotEmpty(message = "{field.cpf.required}")
    private String cpf;
    @NotEmpty(message = "{field.phone.required}")
    private String phone;
    @NotEmpty(message = "{field.rg.required}")
    @Length(min = 9, max = 9)
    private String rg;
    @NotNull(message = "{field.birthdate.required}")
    @Past(message = "{field.birthdate.invalid}")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    @NotEmpty(message = "{field.birthstate.required}")
    private String birthState;
    @NotEmpty
    private boolean isActive;
}
