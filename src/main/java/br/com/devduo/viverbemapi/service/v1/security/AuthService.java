package br.com.devduo.viverbemapi.service.v1.security;

import br.com.devduo.viverbemapi.dtos.LoginRequestDTO;
import br.com.devduo.viverbemapi.dtos.RefreshTokenDTO;
import br.com.devduo.viverbemapi.dtos.RegisterRequestDTO;
import br.com.devduo.viverbemapi.dtos.TokenDTO;
import br.com.devduo.viverbemapi.enums.RoleEnum;
import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ResourceNotFoundException;
import br.com.devduo.viverbemapi.models.Role;
import br.com.devduo.viverbemapi.models.Tenant;
import br.com.devduo.viverbemapi.models.User;
import br.com.devduo.viverbemapi.repository.RoleRepository;
import br.com.devduo.viverbemapi.repository.TenantRepository;
import br.com.devduo.viverbemapi.repository.UserRepository;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService jwtTokenService;

    @Transactional
    public String register(RegisterRequestDTO dto) {
        User existentUser = userRepository.findByEmail(dto.getEmail());
        if (existentUser != null)
            throw new BadRequestException("This email is already taken");

        Role role = roleRepository.findByDescription(RoleEnum.USER);

        Tenant tenant = tenantRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User user = User.builder()
                .tenantId(tenant.getId())
                .email(dto.getEmail())
                .password(new BCryptPasswordEncoder().encode(dto.getPassword()))
                .roles(Set.of(role))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        userRepository.save(user);
        return "User successfully registered";
    }

    public TokenDTO login(LoginRequestDTO dto) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null)
            throw new ResourceNotFoundException("Email/password incorrect or non-existent");

        return jwtTokenService.createAccessToken(user.getEmail(), user.getRoles());
    }

    public TokenDTO refreshToken(RefreshTokenDTO token) {
        DecodedJWT decodedJWT = jwtTokenService.decodedJWT(token.getRefreshToken());

        String email = decodedJWT.getSubject();
        User user = userRepository.findByEmail(email);

        if (user == null)
            throw new ResourceNotFoundException("[ERRO] Usuário não encontrado");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenService.createAccessToken(user.getEmail(), user.getRoles());
    }
}
