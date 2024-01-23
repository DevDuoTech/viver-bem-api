package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, length = 11)
    private String cpf;
    @Column(length = 11)
    private String phone;
    @Column(length = 9)
    private String rg;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "birth_state")
    private String birthState;

    @ManyToOne
    @JoinTable(
            name = "tenant_apartment",
            joinColumns = @JoinColumn(name = "tenant_id"),
            inverseJoinColumns = @JoinColumn(name = "apartment_id")
    )
    private Apartment apartment;
}