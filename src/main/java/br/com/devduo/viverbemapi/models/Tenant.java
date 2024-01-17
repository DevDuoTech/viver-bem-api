package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

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
    @NotEmpty
    private String name;
    @CPF
    @NotEmpty
    @Column(unique = true, length = 11)
    private String cpf;
    @NotEmpty
    @Column(length = 11)
    private String phone;
    @NotEmpty
    @Column(length = 9)
    private String rg;
    @NotEmpty
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @NotEmpty
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