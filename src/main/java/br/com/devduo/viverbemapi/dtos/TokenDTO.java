package br.com.devduo.viverbemapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private Long tenantId;
    private String email;
    private Boolean authenticated;
    private Date createAt;
    private Date expiresAt;
    private String accessToken;
    private String refreshToken;
}
