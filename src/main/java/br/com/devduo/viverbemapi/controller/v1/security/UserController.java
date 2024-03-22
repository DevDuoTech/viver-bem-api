package br.com.devduo.viverbemapi.controller.v1.security;

import br.com.devduo.viverbemapi.service.v1.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PatchMapping(path = "/profile-picture/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Void> updateProfilePicture(
            @RequestPart("profilePicture") MultipartFile profilePicture,
            @PathVariable(value = "id") Long id,
            @RequestHeader("Authorization") String token
    ) {
        userService.updateUserProfilePicture(profilePicture, id, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
