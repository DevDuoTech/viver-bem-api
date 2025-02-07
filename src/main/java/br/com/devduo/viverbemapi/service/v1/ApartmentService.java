package br.com.devduo.viverbemapi.service.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.devduo.viverbemapi.controller.v1.ApartmentController;
import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.enums.StatusApart;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;

@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private PagedResourcesAssembler<Apartment> assembler;

    public PagedModel<EntityModel<Apartment>> findAll(Pageable pageable, StatusApart statusApart) {
        Page<Apartment> apartmentPage = apartmentRepository.findAll(pageable);

        List<Apartment> apartmentList = apartmentPage.toList();

        if (statusApart != null)
            apartmentList = apartmentList.stream()
                    .filter(p -> p.getStatus().equals(statusApart))
                    .collect(Collectors.toList());

        Page<Apartment> apartmentPageFiltered = new PageImpl<>(apartmentList);

        Link link = linkTo(methodOn(ApartmentController.class)
                .findAll(pageable, statusApart))
                .withSelfRel();

        return assembler.toModel(apartmentPageFiltered, link);
    }

    public Apartment findById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found"));
    }

    public Apartment findByNumberAp(Long numberAp) {
        Apartment apartment = apartmentRepository.findByNumberAp(numberAp);
        if (apartment == null)
            throw new ResourceNotFoundException("Resource not found for this apartment number");
        return apartment;
    }

    public void update(ApartmentsRequestDTO apartment) {
        Apartment apartmentToUpdate = findById(apartment.getId());

        apartmentToUpdate.setDescription(apartment.getDescription());
        apartmentToUpdate.setStatus(apartment.getStatus());

        apartmentRepository.save((apartmentToUpdate));
    }

    public void updateStatus(Long apNum) {
        Apartment apartment = findByNumberAp(apNum);
        StatusApart currentStatus = apartment.getStatus();
        StatusApart newStatus = (currentStatus == StatusApart.OCCUPIED) ? StatusApart.AVAILABLE : StatusApart.OCCUPIED;

        apartment.setStatus(newStatus);
        apartmentRepository.save(apartment);
    }
}
