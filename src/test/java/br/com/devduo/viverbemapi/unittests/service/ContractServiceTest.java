package br.com.devduo.viverbemapi.unittests.service;

import br.com.devduo.viverbemapi.dtos.ContractRequestSaveDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.repository.ContractRepository;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import br.com.devduo.viverbemapi.service.v1.ContractService;
import br.com.devduo.viverbemapi.service.v1.TenantService;
import br.com.devduo.viverbemapi.unittests.mocks.ApartmentMocks;
import br.com.devduo.viverbemapi.unittests.mocks.ContractMocks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContractServiceTest {
    @InjectMocks
    private ContractService service;
    @Mock
    private ContractRepository repository;
    @Mock
    private PagedResourcesAssembler<Contract> assembler;
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private TenantService tenantService;

    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Finds all contracts and returns a paged list of all contracts successfully.")
    @SuppressWarnings("unchecked")
    public void testFindAllSuccessfully() {
        Pageable pageable = Mockito.mock(Pageable.class);

        Contract mockedContract = ContractMocks.mockContract();
        Contract mockedContract2 = ContractMocks.mockContract();

        List<Contract> contractList = Arrays.asList(mockedContract, mockedContract2);
        Page<Contract> page = new PageImpl<>(contractList);

        when(repository.findAll(pageable)).thenReturn(page);

        EntityModel<Contract> entityModel1 = EntityModel.of(mockedContract);
        EntityModel<Contract> entityModel2 = EntityModel.of(mockedContract2);
        List<EntityModel<Contract>> entityModels = Arrays.asList(entityModel1, entityModel2);
        PagedModel<EntityModel<Contract>> expectedPagedModel = PagedModel.of(entityModels,
                new PagedModel.PageMetadata(entityModels.size(), 0, 2));

        Mockito.when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expectedPagedModel);

        PagedModel<EntityModel<Contract>> result = service.findAll(pageable);

        Mockito.verify(repository).findAll(pageable);

        assertEquals(expectedPagedModel, result);
        assertEquals(expectedPagedModel.getLinks(), result.getLinks());
        assertEquals(expectedPagedModel.getContent().size(), result.getContent().size());
    }

    @Test
    @DisplayName("Finds a Contract by UUID and returns a Contract Successfully")
    public void testFindContractByUUIDSuccessfully() {
        Contract mockedContract = ContractMocks.mockContract();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(mockedContract));

        var result = service.findByUuid(mockedContract.getUuid());

        assertNotNull(result);
        assertNotNull(result.getUuid());

        assertEquals(mockedContract.getUuid(), result.getUuid());
        assertEquals(mockedContract.getApartment(), result.getApartment());
        assertEquals(mockedContract.getPrice(), result.getPrice());
        assertEquals(mockedContract.getStartDate(), result.getStartDate());
        assertEquals(mockedContract.getEndDate(), result.getEndDate());
    }

    @Test
    @DisplayName("Finds a Contract by UUID and throws a ResourceNotFoundException")
    public void testFindContractByUUIDAndThrowsNotfound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.findByUuid(UUID.randomUUID());
        });

        String expectedMessage = "Resource not found for this UUID";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to find a Contract by null UUID and throws a BadRequestException")
    public void testFindContractByUUIDAndThrowsBadRequest() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.findByUuid(null);
        });

        String expectedMessage = "Contract UUID cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Updates a Contract Successfully")
    public void testUpdateContractSuccessfully() {
        ContractRequestUpdateDTO mockedContractUpdateDTO = ContractMocks.mockContractUpdateDTO();
        Contract existingContract = ContractMocks.mockContract();
        existingContract.setUuid(mockedContractUpdateDTO.getUuid());

        when(repository.findById(mockedContractUpdateDTO.getUuid())).thenReturn(Optional.of(existingContract));
        when(repository.save(existingContract)).thenReturn(existingContract);

        service.update(mockedContractUpdateDTO);

        Contract result = service.findByUuid(mockedContractUpdateDTO.getUuid());

        assertNotNull(result);
        assertNotNull(result.getUuid());

        assertEquals(mockedContractUpdateDTO.getUuid(), result.getUuid());
        assertEquals(mockedContractUpdateDTO.getContractRequestDTO().getPrice(), result.getPrice());
        assertEquals(mockedContractUpdateDTO.getContractRequestDTO().getDueDate(), result.getDueDate());
    }
    
    @Test
    @DisplayName("Tries to update a Contract and throws a BadRequestException")
    public void testUpdateAndThrowsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "ContractRequestDTO cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Persist a new Contract with available Apartment Successfully")
    public void testSaveContractSuccessfully() {
        ContractRequestSaveDTO mockContractSaveDTO = ContractMocks.mockContractSaveDTO();
        Contract mockedContract = ContractMocks.mockContract();
        Contract mockedContract2 = ContractMocks.mockContract();
        mockedContract2.setUuid(UUID.randomUUID());

        Apartment mockedAvailableApartment = ApartmentMocks.mockAvailableApartment();

        when(repository.save(any(Contract.class))).thenReturn(mockedContract);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(mockedContract2));
        when(apartmentService.findByNumberAp(mockedAvailableApartment.getNumberAp())).thenReturn(mockedAvailableApartment);

        String result = service.save(mockContractSaveDTO, mockedAvailableApartment.getNumberAp());

        assertNotNull(result);
        assertEquals("Contract saved successfully", result);
    }

    @Test
    @DisplayName("Tries to persist a new Contract with occupied Apartment and throws a BadRequestException")
    public void testSaveContractThrowsBadRequestException() {
        ContractRequestSaveDTO mockContractSaveDTO = ContractMocks.mockContractSaveDTO();
        Apartment mockedOccupiedApartment = ApartmentMocks.mockOccupiedApartment();

        when(apartmentService.findByNumberAp(mockedOccupiedApartment.getNumberAp())).thenReturn(mockedOccupiedApartment);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.save(mockContractSaveDTO, mockedOccupiedApartment.getNumberAp());
        });

        String expectedMessage = String.format("Apartment %s is currently occupied", mockedOccupiedApartment.getNumberAp());
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to persist a new Contract without contractDTO and throws a BadRequestException")
    public void testSaveContractWithoutArgThrowsBadRequestException() {
        Apartment mockAvailableApartment = ApartmentMocks.mockAvailableApartment();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.save(null, mockAvailableApartment.getNumberAp());
        });

        String expectedMessage = "ContractDTO cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tries to persist a new Contract without Apartment number and throws a BadRequestException")
    public void testSaveContractWithoutApNumThrowsBadRequestException() {
        ContractRequestSaveDTO mockContractSaveDTO = ContractMocks.mockContractSaveDTO();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.save(mockContractSaveDTO, null);
        });

        String expectedMessage = "Apartment number cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
