package br.com.devduo.viverbemapi.models;

import br.com.devduo.viverbemapi.enums.StatusApart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Apartment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "number_ap", unique = true)
    private Long numberAp;
    private String description;
    @Enumerated(EnumType.STRING)
    private StatusApart status;
}

