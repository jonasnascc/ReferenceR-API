package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.user.LoginDTO;
import be.wanna.Referencerback.dto.user.LoginResponseDTO;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.entity.user.UserRole;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.service.authorization.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository repository;

    private final TokenService tokenService;

    @PostMapping("login")
    public ResponseEntity<?> login(
            @RequestBody @Validated LoginDTO dto
    ){
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User)auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("register")
    public ResponseEntity<?> register(
            @RequestBody @Validated LoginDTO dto
    ){
        if(repository.findById(dto.login()).isPresent()) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());

        User newUser = new User(dto.login(), encryptedPassword, UserRole.USER);

        repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
