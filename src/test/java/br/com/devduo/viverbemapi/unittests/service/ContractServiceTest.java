package br.com.devduo.viverbemapi.unittests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.devduo.viverbemapi.dtos.ContractRequestDTO;
import br.com.devduo.viverbemapi.dtos.ContractRequestUpdateDTO;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
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

import br.com.devduo.viverbemapi.models.Contract;
import br.com.devduo.viverbemapi.repository.ContractRepository;
import br.com.devduo.viverbemapi.service.v1.ContractService;
import br.com.devduo.viverbemapi.unittests.mocks.ContractMocks;

@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {
    @InjectMocks
    private ContractService service;
    @Mock
    private ContractRepository repository;
    @Mock
    private PagedResourcesAssembler<Contract> assembler;

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
}
