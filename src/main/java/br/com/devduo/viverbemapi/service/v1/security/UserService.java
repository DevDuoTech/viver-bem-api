package br.com.devduo.viverbemapi.service.v1.security;

import br.com.devduo.viverbemapi.exceptions.BadRequestException;
import br.com.devduo.viverbemapi.exceptions.ForbiddenException;
import br.com.devduo.viverbemapi.models.User;
import br.com.devduo.viverbemapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException("User not found or non-existent");
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsEnabled(),
                user.getIsAccountNonExpired(),
                user.getIsCredentialsNonExpired(),
                user.getIsAccountNonLocked(),
                user.getAuthorities()
        );
    }

    public void updateUserProfilePicture(MultipartFile file, Long id, String token) {
        if (file.isEmpty())
            throw new BadRequestException("The profile picture must be non-null");
        if (token == null)
            throw new BadRequestException("The token must be non-null");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found or doesn't exist"));

        if(isCurrentUser(token, user))
            throw new ForbiddenException("You are not allowed to update another user's profile picture");

        try {
            byte[] bytes = file.getBytes();
            user.setProfilePicture(bytes);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isCurrentUser(String token, User user) {
        String formattedToken = jwtTokenService.formatToken(token);
        String email = jwtTokenService.decodedJWT(formattedToken).getSubject();
        return !email.equals(user.getEmail());
    }
}
