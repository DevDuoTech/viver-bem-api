package br.com.devduo.viverbemapi.unittests.mocks;

import java.time.LocalDate;

import br.com.devduo.viverbemapi.dtos.TenantsRequestDTO;
import br.com.devduo.viverbemapi.models.Tenant;

public class TenantMocks {
    public static Tenant mockTenant() {
        return Tenant.builder()
                .id(1L)
                .name("Tenant name")
                .cpf("00000000000")
                .phone("999999999")
                .rg("111111111")
                .birthDate(LocalDate.of(1995, 5, 20))
                .birthLocal("São Paulo")
//                .apartment(ApartmentMocks.mockOccupiedApartment())
                .build();
    }

    public static TenantsRequestDTO mockTenantDTO() {
        return TenantsRequestDTO.builder()
                .name("Tenant dto")
                .cpf("11111111111")
                .phone("888888888")
                .rg("111111111")
                .birthDate(LocalDate.of(1995, 5, 20))
                .birthState("São Paulo")
                .build();
    }
}
