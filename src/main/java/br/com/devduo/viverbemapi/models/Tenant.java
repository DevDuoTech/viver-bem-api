package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    @Column(length = 9, unique = true)
    private String rg;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "birth_local")
    private String birthLocal;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"tenant"})
    @JoinColumn(name = "contract_id", referencedColumnName = "uuid")
    private Contract contract;

    @JsonIgnore
    @JsonIgnoreProperties({"tenant"})
    @OneToMany(mappedBy = "tenant")
    private List<Payment> payments;
}