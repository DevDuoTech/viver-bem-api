package br.com.devduo.viverbemapi.unittests.mocks;

import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.models.Apartment;

public class ApartmentMocks {
    public static Apartment mockOccupiedApartment() {
        return Apartment.builder()
                .id(1L)
                .description("A random apartment")
                .numberAp(500L)
                .status(StatusApart.OCCUPIED)
                .build();
    }

    public static Apartment mockAvailableApartment() {
        return Apartment.builder()
                .id(1L)
                .description("A random apartment")
                .numberAp(500L)
                .status(StatusApart.AVAILABLE)
                .build();
    }
}
