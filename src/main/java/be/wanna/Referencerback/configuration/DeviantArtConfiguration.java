package be.wanna.Referencerback.configuration;

import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class DeviantArtConfiguration {
    @Value("${deviant.art.url}")
    private String url;

    @Value("${deviant.art.login.page.path}")
    private String loginPagePath;

    @Value("${deviant.art.login.url}")
    private String loginUrl;
    @Bean
    public CommandLineRunner createDeviantArtProvider(@Autowired ProviderRepository repository){
        return args -> {
            Optional<Provider> optProvider = repository.findById("deviantart");
            if (optProvider.isEmpty()) {
                repository.save(new Provider("deviantart", url, loginUrl, loginPagePath));
            }
        };
    }
}
