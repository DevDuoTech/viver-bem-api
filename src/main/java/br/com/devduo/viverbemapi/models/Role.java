package br.com.devduo.viverbemapi.models;

import br.com.devduo.viverbemapi.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private RoleEnum description;
    @Override
    public String getAuthority() {
        return "ROLE_" + description.toString();
    }
}
