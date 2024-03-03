package br.com.devduo.viverbemapi.service.v1.security;

import br.com.devduo.viverbemapi.dtos.TokenDTO;
import br.com.devduo.viverbemapi.models.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class JwtTokenService {
    @Value("${security.jwt.token.secret}")
    private String SECRET_KEY;
    @Value("${security.jwt.token.time}")
    private Long VALIDITY_TIME;

    @Autowired
    private UserService userService;

    public TokenDTO createAccessToken(String email, Set<Role> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_TIME);
        String accessToken = getAccessToken(email, roles, now, validity);
        String refreshToken = getRefreshToken(email, roles, now);

        return new TokenDTO().builder()
                .email(email)
                .authenticated(true)
                .createAt(now)
                .expiresAt(validity)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getAccessToken(String email, Set<Role> roles, Date now, Date validity) {
        List<String> rolesAsString = roles.stream()
                .map(r -> r.getDescription().toString())
                .toList();

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("roles", rolesAsString)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String getRefreshToken(String email, Set<Role> roles, Date now) {
        List<String> rolesAsString = roles.stream()
                .map(r -> r.getDescription().toString())
                .toList();
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + VALIDITY_TIME * 2))
                .withClaim("roles", rolesAsString)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String formatToken(String token) {
        if (token.startsWith("Bearer "))
            return token.substring("Bearer ".length());
        return null;
    }

    public DecodedJWT decodedJWT(String token) {
        JWTVerifier jwtVerifier = JWT
                .require(Algorithm.HMAC256(SECRET_KEY))
                .build();
        return jwtVerifier.verify(token);
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = decodedJWT(token);
            return new Date().before(decodedJWT.getExpiresAt());
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedJWT(token);
        UserDetails user = userService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }
}
