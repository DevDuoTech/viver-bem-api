package br.com.devduo.viverbemapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Contract extends BaseEntity {
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
    private Double price;
    private String description;
    @Column(name = "due_date")
    private Integer dueDate;
    @Column(name = "has_guarantee")
    private Boolean hasGuarantee;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tenant_id", referencedColumnName = "id")
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "number_ap", referencedColumnName = "number_ap")
    private Apartment apartment;
}
