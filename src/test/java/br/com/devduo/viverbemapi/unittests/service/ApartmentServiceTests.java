package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApartmentServiceTests {
    @InjectMocks
    private ApartmentService apartmentService;
    @Mock
    private ApartmentRepository apartmentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Finds a Apartment by ID and returns a apartment successfully")
    public void testFindByIdSuccessfully() {
        Apartment mockedApartment = mockApartment();

        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(mockedApartment));

        Apartment apartment = apartmentService.findById(1L);

        assertNotNull(apartment);
        assertNotNull(apartment.getId());

        assertEquals(1L, apartment.getId());
        assertEquals("A random apartment", apartment.getDescription());
        assertEquals(500L, apartment.getNumberAp());
        assertEquals(StatusApart.OCCUPIED, apartment.getStatus());
    }

    @Test
    @DisplayName("Tries to find a Apartment by ID and throws a ResourceNotFoundException")
    public void testFindByIdAndThrowsNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            apartmentService.findById(1L);
        });

        String expectedMessage = "No records found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    public Apartment mockApartment() {
        return Apartment.builder()
                .id(1L)
                .description("A random apartment")
                .numberAp(500L)
                .status(StatusApart.OCCUPIED)
                .build();
    }
}
