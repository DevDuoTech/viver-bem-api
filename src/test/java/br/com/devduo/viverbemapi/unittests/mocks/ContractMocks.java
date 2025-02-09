package br.com.devduo.viverbemapi.unittests.mocks;

import java.time.LocalDate;
import java.util.UUID;

import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.models.Contract;

public class ContractMocks {
    public static Contract mockContract() {
        return Contract.builder()
                .uuid(UUID.randomUUID())
                .startDate(LocalDate.of(2024, 1, 5))
                .endDate(LocalDate.of(2024, 6, 5))
                .price(500.0)
                .dueDate(5)
                .description("A simple contract")
                .hasGuarantee(true)
                .apartment(ApartmentMocks.mockOccupiedApartment())
                .tenant(TenantMocks.mockActiveTenant())
                .build();
    }

    public static ContractRequestDTO mockContractDTO() {
        return ContractRequestDTO.builder()
                .startDate(LocalDate.of(2024, 1, 5))
                .endDate(LocalDate.of(2024, 6, 5))
                .price(500.0)
                .dueDate(15)
                .hasGuarantee(true)
                .description("A simple contract")
                .build();
    }

    public static ContractRequestUpdateDTO mockContractUpdateDTO() {
        return ContractRequestUpdateDTO.builder()
                .uuid(mockContract().getUuid())
                .contractRequestDTO(mockContractDTO())
                .build();
    }

    public static ContractRequestSaveDTO mockContractSaveDTO() {
        return ContractRequestSaveDTO.builder()
                .contractRequestDTO(mockContractDTO())
                .tenantsRequestDTO(TenantMocks.mockActiveTenantDTO())
                .build();
    }
}
