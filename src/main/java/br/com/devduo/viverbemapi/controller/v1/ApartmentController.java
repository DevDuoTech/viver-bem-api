package br.com.devduo.viverbemapi.controller.v1;

import br.com.devduo.viverbemapi.dtos.ApartmentRequest;
import br.com.devduo.viverbemapi.models.Apartments;
import br.com.devduo.viverbemapi.service.v1.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @GetMapping
    public ResponseEntity<List<Apartments>> findAll(){
        return ResponseEntity.ok(apartmentService.findAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Apartments> findById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(apartmentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Apartments> save(@RequestBody Apartments apartment){
        return new ResponseEntity<>(apartmentService.save(apartment), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody ApartmentRequest apartment){
        apartmentService.update(apartment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id){
        apartmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
