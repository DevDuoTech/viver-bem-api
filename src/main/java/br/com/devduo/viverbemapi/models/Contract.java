package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", updatable = false, unique = true, nullable = false)
    private UUID uuid;
    @Column(name = "start_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @Column(name = "end_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    private BigDecimal price;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "has_guarantee")
    private Boolean hasGuarantee;

    @JsonIgnoreProperties({"tenant"})
    @OneToOne(mappedBy = "contract")
    private Tenant tenant;

    @ManyToOne
    @JoinTable(
            name = "contract_apartment",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "apartment_id")
    )
    private Apartment apartment;
}
