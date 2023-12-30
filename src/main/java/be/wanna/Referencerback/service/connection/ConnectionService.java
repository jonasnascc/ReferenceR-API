package be.wanna.Referencerback.service.connection;

import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.User;
import be.wanna.Referencerback.entity.connection.Connection;
import be.wanna.Referencerback.entity.connection.Cookie;
import be.wanna.Referencerback.entity.connection.CsrfToken;
import be.wanna.Referencerback.repository.connection.ConnectionRepository;
import be.wanna.Referencerback.repository.connection.CookieRepository;
import be.wanna.Referencerback.repository.ProviderRepository;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.repository.connection.CsrfTokenRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService{
    private final UserRepository userRepository;

    private final ConnectionRepository connectionRepository;

    private final ProviderRepository providerRepository;

    private final CookieRepository cookieRepository;

    private final CsrfTokenRepository csrfTokenRepository;

    @Value("${user.agent}")
    private String userAgent;

    public Long newConnection(String username, String password, String providerName) throws Exception {
        User user;

        Optional<User> optUser = userRepository.findById(username);
        if(optUser.isPresent()){
            int correctHash = Integer.parseInt(optUser.get().getPassword());
            int dtoHash = password.hashCode();
            if(dtoHash != correctHash) throw new RuntimeException("Invalid Password.");
            user = optUser.get();
        }
        else user = userRepository.save(new User(username, Integer.toString(password.hashCode())));

        Provider provider = providerRepository.findById(providerName).orElseThrow(() -> new RuntimeException("Provider(%s) not found.".formatted(providerName)));
        provider.setUser(user);

        List<Cookie> cookies = getNewCookies(username, password, provider);

        Connection connection = new Connection();
        connection.setProvider(provider);
        connection.setRemember(true);
        Connection savedConn = connectionRepository.save(connection);

        cookies.forEach(cookie -> {
            cookie.setConnection(savedConn);
            cookieRepository.save(cookie);
        });
        savedConn.setCookies(cookies);

        return connectionRepository.save(savedConn).getId();
    }
//
    public List<Cookie> getNewCookies(String username, String password, Provider provider) throws Exception {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("referer", provider.getUrl());
        formData.put("username", username);
        formData.put("password",password);
        formData.put("remember", "on");

        org.jsoup.Connection.Response response;
        try {
            response = Jsoup.connect((provider.getUrl() + provider.getLoginPath()))
                    .method(org.jsoup.Connection.Method.GET)
                    .execute();
            Map<String, String> cookies = response.cookies();

            String csrfToken = response.parse().select("input[name=csrf_token]").val();
            formData.put("csrf_token", csrfToken);

            org.jsoup.Connection.Response login = Jsoup.connect(provider.getLoginUrl())
                    .cookies(cookies)
                    .userAgent(userAgent)
                    .data(formData)
                    .method(org.jsoup.Connection.Method.POST)
                    .execute();

            return login.cookies().keySet().stream().map(c -> {

                Cookie cookie = new Cookie(provider.getName() + "-" + c, login.cookies().get(c));
                cookie.setCsrfToken(csrfTokenRepository.save(new CsrfToken(csrfToken)));
                cookieRepository.save(cookie);

                return cookie;

            }).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Ocorreu um erro ao conectar com o provedor: " + e.getMessage());
        }
    }

}
