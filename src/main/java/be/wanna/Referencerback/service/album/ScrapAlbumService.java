package be.wanna.Referencerback.service.album;

import be.wanna.Referencerback.dto.album.ScrapAlbumDTO;
import be.wanna.Referencerback.dto.AuthorDTO;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.album.ScrapAlbum;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.repository.*;
import be.wanna.Referencerback.service.author.AuthorService;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScrapAlbumService {

    private final UserRepository userRepository;

    private final DeviantArtService deviantArtService;

    private final ScrapAlbumRepository scrapAlbumRepository;

    private final PhotoRepository photoRepository;

    private final ProviderRepository providerRepository;

    private final AuthorService authorService;

    private final AuthorRepository authorRepository;

    public List<ScrapAlbumDTO> getAuthorAlbums(String author, String provider){
        if (provider.equals("deviantart")) {
            return deviantArtService.findUserAlbums(author).stream().map(dto -> new ScrapAlbumDTO(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.url(),
                dto.thumbnail(),
                dto.author(),
                dto.provider(),
                dto.size()
            )).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public Set<Photo> listPhotos(String author, String albumId, int page, int limit, String provider, Integer maxThumbsize){
        if (provider.equals("deviantart")) {
            return deviantArtService.listAlbumPhotosByPage(albumId, author, page, Math.min(limit, 60), maxThumbsize);
        }

        return Collections.emptySet();

    }

    public PhotoDTO getAlbumThumbnail(Long albumId){
        ScrapAlbum album = scrapAlbumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("Album not found in database."));
        Photo thumb = album.getThumbnailPhoto();
        return deviantArtService.getDeviationWithToken(thumb, album.getAuthor().getName());
    }

    private ScrapAlbum convertScrapAlbum(ScrapAlbumDTO dto) {
        Provider provider = providerRepository.findById(dto.provider()).orElseThrow(() -> new RuntimeException("Album provider not found in database"));
        Optional<Author> optAuthor = authorRepository.findAuthorByNameAndProvider(dto.author(), dto.provider());
        PhotoDTO thumbDTO = dto.thumbnail();

        Photo thumbnail = getAlbumThumbnail(dto, thumbDTO);

        Author author;
        if(optAuthor.isEmpty()) {
            String authorId = authorService.save(new AuthorDTO(null, dto.author(), "", dto.provider()));
            author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found in database."));
        } else author = optAuthor.get();


        return new ScrapAlbum(
                dto.code(),
                dto.name(),
                dto.url(),
                dto.size(),
                thumbnail,
                author,
                provider
        );
    }

    private Photo getAlbumThumbnail(ScrapAlbumDTO dto, PhotoDTO thumbDTO) {
        Photo thumbnail = null;
        if(thumbDTO.id() != null) {
            Optional<Photo> optThumb = photoRepository.findById(dto.thumbnail().id());

            if(optThumb.isPresent()) {
                thumbnail = optThumb.get();
            }
        } else {
            int tokenIndex = thumbDTO.url().indexOf("?token");

            String thumbUrl = thumbDTO.url()
                    .substring(0, tokenIndex != -1 ? tokenIndex : thumbDTO.url().length());

             thumbnail =  photoRepository.save(new Photo(
                     thumbDTO.code(),
                     thumbDTO.title(),
                     thumbUrl.substring(0, Math.min(thumbUrl.length(), 255)),
                     thumbDTO.mature()
             ));
        }
        return thumbnail;
    }

    public static ScrapAlbumDTO convertDTO(ScrapAlbum album) {
        return new ScrapAlbumDTO(
                album.getId(),
                album.getCode(),
                album.getName(),
                album.getUrl(),
                null,
                album.getAuthor().getName(),
                album.getProvider().getName(),
                album.getSize()
        );
    }

}
