package br.com.devduo.viverbemapi.service.v1;

import br.com.devduo.viverbemapi.dtos.ApartmentRequest;
import br.com.devduo.viverbemapi.models.Apartment;
import br.com.devduo.viverbemapi.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;

    public List<Apartment> findAll(){
        return apartmentRepository.findAll();
    }

    public Apartment findById(Long id){
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("No records found"));
    }

    public Apartment save(Apartment apartment){
        return apartmentRepository.save(apartment);
    }

    public void update(ApartmentRequest apartment){
        Apartment apartmentToUpdate = findById(apartment.getId());

        apartmentToUpdate.setDescription(apartment.getDescription());
        apartmentToUpdate.setStatus(apartment.getStatus());

        apartmentRepository.save((apartmentToUpdate));
    }

    public void delete(Long id){
        apartmentRepository.deleteById(id);
    }
}
