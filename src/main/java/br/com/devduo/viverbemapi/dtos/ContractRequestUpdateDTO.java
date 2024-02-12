package br.com.devduo.viverbemapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestUpdateDTO {
    private UUID uuid;
    @Valid
    @NotNull(message = "{field.contract.required}")
    private ContractRequestDTO contractRequestDTO;
}
