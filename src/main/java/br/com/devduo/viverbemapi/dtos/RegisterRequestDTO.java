package br.com.devduo.viverbemapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 8)
    private String password;
}
