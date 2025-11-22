package br.com.devduo.viverbemapi.controller.v1.security;

import br.com.devduo.viverbemapi.dtos.LoginRequestDTO;
import br.com.devduo.viverbemapi.dtos.RefreshTokenDTO;
import br.com.devduo.viverbemapi.dtos.RegisterRequestDTO;
import br.com.devduo.viverbemapi.dtos.TokenDTO;
import br.com.devduo.viverbemapi.service.v1.security.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoint to Managing user Authentications")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO dto) {
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refresh(@RequestBody RefreshTokenDTO token) {
        return ResponseEntity.ok(authService.refreshToken(token));
    }

}
