package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.controller.v1.ApartmentController;
import br.com.devduo.viverbemapi.dtos.ApartmentsRequestDTO;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private PagedResourcesAssembler<Apartment> assembler;

    public PagedModel<EntityModel<Apartment>> findAll(Pageable pageable){
        Page<Apartment> apartmentPage = apartmentRepository.findAll(pageable);

        Link link = linkTo(methodOn(ApartmentController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();

        return assembler.toModel(apartmentPage, link);
    }

    public Apartment findById(Long id){
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found"));
    }

    public void update(ApartmentsRequestDTO apartment){
        Apartment apartmentToUpdate = findById(apartment.getId());

        apartmentToUpdate.setDescription(apartment.getDescription());
        apartmentToUpdate.setStatus(apartment.getStatus());

        apartmentRepository.save((apartmentToUpdate));
    }

}
