package io.flowinquiry.modules.usermanagement.controller;

import io.flowinquiry.modules.usermanagement.InvalidLoginException;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.UserService;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import io.flowinquiry.security.service.JwtService;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final JwtService jwtService;

    private final AuthenticationManager appAuthenticationManager;

    private final UserService userService;

    private final UserRepository userRepository;

    public LoginController(
            JwtService jwtService,
            @Qualifier("appAuthenticationManager") AuthenticationManager appAuthenticationManager,
            UserService userService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.appAuthenticationManager = appAuthenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getEmail(), loginVM.getPassword());

        Authentication authentication = appAuthenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDTO adminUserDTO =
                userService.getUserWithAuthorities().orElseThrow(InvalidLoginException::new);

        String jwt = jwtService.generateToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);

        // Update last login time for user
        userRepository.updateLastLoginTime(loginVM.getEmail(), Instant.now());
        return new ResponseEntity<>(adminUserDTO, httpHeaders, HttpStatus.OK);
    }
}
