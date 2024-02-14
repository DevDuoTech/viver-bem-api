package br.com.devduo.viverbemapi.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import br.com.devduo.viverbemapi.unittests.mocks.ApartmentMocks;

@ExtendWith(MockitoExtension.class)
public class ApartmentServiceTest {
    @InjectMocks
    private ApartmentService apartmentService;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private PagedResourcesAssembler<Apartment> assembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Finds all available Apartments and return a PagedModel of the available Apartments successfully")
    public void testFindAllWithArgsSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);

        StatusApart statusApart = StatusApart.AVAILABLE;

        Apartment apartment1 = ApartmentMocks.mockAvailableApartment();
        Apartment apartment2 = ApartmentMocks.mockOccupiedApartment();
        List<Apartment> apartmentList = Arrays.asList(apartment1, apartment2);
        Page<Apartment> page = new PageImpl<>(apartmentList);

        Mockito.when(apartmentRepository.findAll(pageable)).thenReturn(page);

        EntityModel<Apartment> entityModel1 = EntityModel.of(apartment1);
        EntityModel<Apartment> entityModel2 = EntityModel.of(apartment2);
        List<EntityModel<Apartment>> entityModels = Arrays.asList(entityModel1, entityModel2);
        PagedModel<EntityModel<Apartment>> expectedPagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(entityModels.size(), 0, 2));

        Mockito.when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expectedPagedModel);

        PagedModel<EntityModel<Apartment>> result = apartmentService.findAll(pageable, statusApart);

        Mockito.verify(apartmentRepository).findAll(pageable);

        assertEquals(expectedPagedModel, result);

        List<EntityModel<Apartment>> expectedList = expectedPagedModel.getContent().stream().toList();
        Apartment expectedApartment = expectedList.get(0).getContent();

        List<EntityModel<Apartment>> resultList = result.getContent().stream().toList();
        Apartment resultApartment = resultList.get(0).getContent();

        assertEquals(expectedList.size(), resultList.size());

        assertEquals(expectedApartment.getDescription(), resultApartment.getDescription());
        assertEquals(expectedApartment.getNumberAp(), resultApartment.getNumberAp());
        assertEquals(expectedApartment.getStatus(), resultApartment.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Finds all available Apartments and return a PagedModel of the available Apartments successfully")
    public void testFindAllWithoutArgsSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);

        Apartment apartment1 = ApartmentMocks.mockAvailableApartment();
        Apartment apartment2 = ApartmentMocks.mockOccupiedApartment();
        List<Apartment> apartmentList = Arrays.asList(apartment1, apartment2);
        Page<Apartment> page = new PageImpl<>(apartmentList);

        Mockito.when(apartmentRepository.findAll(pageable)).thenReturn(page);

        EntityModel<Apartment> entityModel1 = EntityModel.of(apartment1);
        EntityModel<Apartment> entityModel2 = EntityModel.of(apartment2);
        List<EntityModel<Apartment>> entityModels = Arrays.asList(entityModel1, entityModel2);
        PagedModel<EntityModel<Apartment>> expectedPagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(entityModels.size(), 0, 2));

        Mockito.when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expectedPagedModel);

        PagedModel<EntityModel<Apartment>> result = apartmentService.findAll(pageable, null);

        Mockito.verify(apartmentRepository).findAll(pageable);

        assertEquals(expectedPagedModel, result);

        List<EntityModel<Apartment>> expectedList = expectedPagedModel.getContent().stream().toList();
        Apartment expectedApartment = expectedList.get(0).getContent();

        List<EntityModel<Apartment>> resultList = result.getContent().stream().toList();
        Apartment resultApartment = resultList.get(0).getContent();

        assertEquals(expectedList.size(), resultList.size());

        assertEquals(expectedApartment.getDescription(), resultApartment.getDescription());
        assertEquals(expectedApartment.getNumberAp(), resultApartment.getNumberAp());
        assertEquals(expectedApartment.getStatus(), resultApartment.getStatus());
    }

    @Test
    @DisplayName("Finds a Apartment by ID and returns a apartment successfully")
    public void testFindByIdSuccessfully() {
        Apartment mockedApartment = ApartmentMocks.mockOccupiedApartment();

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

    @Test
    @DisplayName("Finds a Apartment by number and returns a apartment successfully")
    public void testFindByNumberApSuccessfully() {
        Apartment mockApartment = ApartmentMocks.mockOccupiedApartment();

        when(apartmentRepository.findByNumberAp(500L)).thenReturn(mockApartment);

        Apartment ap = apartmentService.findByNumberAp(500L);

        assertNotNull(ap);
        assertNotNull(ap.getId());

        assertEquals(mockApartment.getId(), ap.getId());
        assertEquals(mockApartment.getDescription(), ap.getDescription());
        assertEquals(mockApartment.getNumberAp(), ap.getNumberAp());
        assertEquals(mockApartment.getNumberAp(), ap.getNumberAp());
    }

    @Test
    @DisplayName("Tries to find a Apartment by number and throws a ResourceNotFoundException")
    public void testFindByNumberApAndThrowsNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            apartmentService.findByNumberAp(500L);
        });

        String expectedMessage = "Resource not found for this apartment number";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Updates a Apartment status and description successfully")
    public void testUpdateSuccessfully() {
        ApartmentsRequestDTO requestDTO = new ApartmentsRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setDescription("Updated Description");
        requestDTO.setStatus(StatusApart.AVAILABLE);

        Apartment existingApartment = ApartmentMocks.mockOccupiedApartment();
        existingApartment.setDescription("Old Description");

        Mockito.when(apartmentRepository.findById(1L)).thenReturn(java.util.Optional.of(existingApartment));

        apartmentService.update(requestDTO);

        Mockito.verify(apartmentRepository).save(existingApartment);

        assertEquals("Updated Description", existingApartment.getDescription());
        assertEquals(StatusApart.AVAILABLE, existingApartment.getStatus());
    }

    @Test
    @DisplayName("Update Apartment status to Available successfully")
    public void testUpdateStatusToAvailable() {
        Apartment existingApartment = ApartmentMocks.mockOccupiedApartment();

        Mockito.when(apartmentRepository.findByNumberAp(500L)).thenReturn(existingApartment);

        apartmentService.updateStatus(500L);

        Mockito.verify(apartmentRepository).save(existingApartment);

        assertEquals(StatusApart.AVAILABLE, existingApartment.getStatus());
    }

    @Test
    @DisplayName("Update Apartment status to Occupied successfully")
    public void testUpdateStatusToOccupied() {
        Apartment existingApartment = ApartmentMocks.mockAvailableApartment();

        Mockito.when(apartmentRepository.findByNumberAp(500L)).thenReturn(existingApartment);

        apartmentService.updateStatus(500L);

        Mockito.verify(apartmentRepository).save(existingApartment);

        assertEquals(StatusApart.OCCUPIED, existingApartment.getStatus());
    }
}
