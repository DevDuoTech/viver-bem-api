package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import br.com.devduo.viverbemapi.unittests.mocks.TenantMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {
    @InjectMocks
    private TenantService service;
    @Mock
    private TenantRepository repository;
    @Mock
    private PagedResourcesAssembler<Tenant> assembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Finds all Tenants and return a PagedModel of Tenants successfully")
    public void testFindAllSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Tenant mockedTenant = TenantMocks.mockTenant();
        Tenant mockedTenant2 = TenantMocks.mockTenant();

        List<Tenant> tenantList = List.of(mockedTenant, mockedTenant2);

        Page<Tenant> tenantPage = new PageImpl<>(tenantList);

        when(repository.findAll(pageable)).thenReturn(tenantPage);

        EntityModel<Tenant> entityModel1 = EntityModel.of(mockedTenant);
        EntityModel<Tenant> entityModel2 = EntityModel.of(mockedTenant2);
        List<EntityModel<Tenant>> entityModels = Arrays.asList(entityModel1, entityModel2);
        PagedModel<EntityModel<Tenant>> expectedPagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(entityModels.size(), 0, 2));

        Mockito.when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expectedPagedModel);

        PagedModel<EntityModel<Tenant>> result = service.findAll(pageable);

        Mockito.verify(repository).findAll(pageable);

        assertEquals(expectedPagedModel, result);

        List<EntityModel<Tenant>> expectedList = expectedPagedModel.getContent().stream().toList();
        Tenant expectedTenant = expectedList.get(0).getContent();

        List<EntityModel<Tenant>> resultList = result.getContent().stream().toList();
        Tenant resultTenant = resultList.get(0).getContent();

        assertEquals(expectedList.size(), resultList.size());
        assertEquals(expectedTenant.getId(), resultTenant.getId());
        assertEquals(expectedTenant.getCpf(), resultTenant.getCpf());
        assertEquals(expectedTenant.getRg(), resultTenant.getRg());
        assertEquals(expectedTenant.getApartment().getNumberAp(), resultTenant.getApartment().getNumberAp());
        assertEquals(expectedTenant.getApartment().getStatus(), resultTenant.getApartment().getStatus());
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
