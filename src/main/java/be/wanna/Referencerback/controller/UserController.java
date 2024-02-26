package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.TokenDTO;
import be.wanna.Referencerback.dto.user.LoginDTO;
import be.wanna.Referencerback.dto.user.LoginResponseDTO;
import be.wanna.Referencerback.dto.user.UserDTO;
import be.wanna.Referencerback.entity.token.Token;
import be.wanna.Referencerback.entity.token.TokenType;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.entity.user.UserRole;
import be.wanna.Referencerback.repository.TokenRepository;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.service.authorization.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository repository;

    private final TokenService tokenService;

    private final TokenRepository tokenRepository;

    @PostMapping("login")
    public ResponseEntity<?> login(
            @RequestBody @Validated LoginDTO dto
    ){
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User)auth.getPrincipal();

        String token = tokenService.generateToken(user);

        tokenService.revokeAllUserTokens(user);
        saveUserToken(user, token);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("register")
    public ResponseEntity<?> register(
            @RequestBody @Validated LoginDTO dto
    ){
        if(repository.findByLogin(dto.login()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());

        User newUser = new User(dto.login(), encryptedPassword, UserRole.USER);
        User savedUser = repository.save(newUser);

        String token = tokenService.generateToken(savedUser);
        saveUserToken(savedUser, token);

        return ResponseEntity.ok().build();
    }

    private void saveUserToken(User user, String token) {
        tokenRepository.save(new Token(token, TokenType.BEARER, false, false, user));
    }

    @GetMapping("token/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        String login = tokenService.validateToken(token);
        User user = repository.findByLogin(login);

        return ResponseEntity.ok(new UserDTO(user.getId(), user.getLogin(), user.getRole()));
    }
}
