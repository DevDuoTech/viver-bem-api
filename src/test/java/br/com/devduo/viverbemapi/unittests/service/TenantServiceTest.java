package br.com.devduo.viverbemapi.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import br.com.devduo.viverbemapi.unittests.mocks.TenantMocks;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {
    @InjectMocks
    private TenantService service;
    @Mock
    private TenantRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Finds a Tenant by ID and returns a Tenant successfully")
    public void testFindByIdSuccessfully() {
        Tenant existingTenant = TenantMocks.mockTenant();

        when(repository.findById(1L)).thenReturn(Optional.of(existingTenant));

        Tenant result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());

        assertEquals(existingTenant.getCpf(), result.getCpf());
        assertEquals(existingTenant.getId(), result.getId());
        assertEquals(existingTenant.getRg(), result.getRg());
        assertEquals(existingTenant.getApartment(), result.getApartment());
    }

    @Test
    @DisplayName("Tries to find a Tenant by ID and throws a ResourceNotFoundException")
    public void testFindByIdAndThrowsNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });

        String expectedMessage = "No records found for this ID";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Persist a new Tenant and returns a Tenant successfully")
    public void testSaveSuccessully() {
        Tenant persistedTenant = TenantMocks.mockTenant();

        when(repository.save(persistedTenant)).thenReturn(persistedTenant);

        Tenant result = service.save(persistedTenant);

        verify(repository).save(persistedTenant);

        assertNotNull(result);

        assertEquals(persistedTenant.getId(), result.getId());
        assertEquals(persistedTenant.getCpf(), result.getCpf());
        assertEquals(persistedTenant.getRg(), result.getRg());
        assertEquals(persistedTenant.getApartment(), result.getApartment());
    }

    @Test
    @DisplayName("Persist a null Tenant and throws an BadRequestException")
    public void testSaveWithNull() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.save(null);
        });

        String expectedMessage = "Tenant cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
