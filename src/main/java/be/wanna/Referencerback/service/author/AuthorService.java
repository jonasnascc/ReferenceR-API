package be.wanna.Referencerback.service.author;

import be.wanna.Referencerback.dto.AuthorDTO;
import be.wanna.Referencerback.dto.AuthorProfileDTO;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.repository.AuthorRepository;
import be.wanna.Referencerback.repository.ProviderRepository;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    private final ProviderRepository providerRepository;

    private final DeviantArtService deviantArtService;

    public String save(AuthorDTO dto) {
        Optional<Author> optAuthor = authorRepository.findAuthorByNameAndProvider(dto.name(), dto.providerName());
        if(optAuthor.isPresent()) throw new RuntimeException("Author already exists.");

        Provider provider = providerRepository.findById(dto.providerName()).orElseThrow(() -> new RuntimeException("Provider not found in database."));

        Author author = new Author(dto.name(), dto.profileUrl(), provider);

        return authorRepository.save(author).getId();
    }

    public AuthorProfileDTO getAuthorProfile(String authorName){
        return deviantArtService.getAuthorProfile(authorName);
    }
}
