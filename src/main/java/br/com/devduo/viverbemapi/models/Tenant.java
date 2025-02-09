package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    @Column(unique = true, length = 11)
    private String cpf;
    @Column(length = 11)
    private String phone;
    @Column(length = 9, unique = true)
    private String rg;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "birth_local")
    private String birthLocal;
    @Column(name = "is_active")
    private Boolean isActive = true;

    @JsonIgnore
    @OneToOne(mappedBy = "tenant")
    private Contract contract;
}