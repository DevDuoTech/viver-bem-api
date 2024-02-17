package br.com.devduo.viverbemapi.unittests.mocks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.models.Contract;

public class ContractMocks {

    public static Contract mockContract() {
        return Contract.builder()
                .uuid(UUID.fromString("45b5e2d4-fa97-4eaf-afb9-3f8378ed8cb8"))
                .startDate(LocalDate.of(2024, 1, 05))
                .endDate(LocalDate.of(2024, 6, 05))
                .price(BigDecimal.valueOf(550))
                .description("A simple contract")
                .apartment(ApartmentMocks.mockAvailableApartment())
                .build();
    }

    public static ContractRequestDTO mockContractDTO() {
        return ContractRequestDTO.builder()
                .startDate(LocalDate.of(2024, 1, 05))
                .endDate(LocalDate.of(2024, 6, 05))
                .price(BigDecimal.valueOf(550))
                .description("A simple contract")
                .build();
    }
}
