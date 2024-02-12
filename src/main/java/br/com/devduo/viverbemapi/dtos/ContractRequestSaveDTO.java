package br.com.devduo.viverbemapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestSaveDTO {
    @Valid
    @NotNull(message = "{field.contract.required}")
    private ContractRequestDTO contractRequestDTO;
    @Valid
    @NotNull(message = "{field.tenant.required}")
    private TenantsRequestDTO tenantsRequestDTO;
}
