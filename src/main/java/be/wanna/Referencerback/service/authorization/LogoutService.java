package be.wanna.Referencerback.service.authorization;

import be.wanna.Referencerback.entity.token.Token;
import be.wanna.Referencerback.repository.TokenRepository;
import be.wanna.Referencerback.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication
    ) {
        String token = this.recoverToken(request);
        if(token != null){
            boolean isValidToken =  tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            if(isValidToken) {
                String login = tokenService.validateToken(token);
                UserDetails user = userRepository.findByLogin(login);
                if(user != null){
                    Token storedToken = tokenRepository.findByToken(token).orElse(null);
                    if(storedToken != null) {
                        storedToken.setExpired(true);
                        storedToken.setRevoked(true);
                        tokenRepository.save(storedToken);
                    }
                }
            }
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
