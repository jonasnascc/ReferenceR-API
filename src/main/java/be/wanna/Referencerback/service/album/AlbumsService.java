package be.wanna.Referencerback.service.album;

import be.wanna.Referencerback.dto.album.AlbumDTO;
import be.wanna.Referencerback.dto.AuthorDTO;
import be.wanna.Referencerback.dto.photo.PhotoDTO;
import be.wanna.Referencerback.entity.album.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.Favorites;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.*;
import be.wanna.Referencerback.service.author.AuthorService;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AlbumsService {

    private final UserRepository userRepository;

    private final DeviantArtService deviantArtService;

    private final AlbumRepository albumRepository;

    private final PhotoRepository photoRepository;

    private final ProviderRepository providerRepository;

    private final AuthorService authorService;

    private final AuthorRepository authorRepository;

    public List<AlbumDTO> getAuthorAlbums(String author, String provider){
        if (provider.equals("deviantart"))
            return deviantArtService.findUserAlbums(author);

        return Collections.emptyList();
    }

    public Set<PhotoDTO> listPhotos(String author, String albumCode, int page, int limit, String provider, Integer maxThumbsize){
        if (provider.equals("deviantart"))
            return deviantArtService.listAlbumPhotosByPage(albumCode, author, page, Math.min(limit, 60), maxThumbsize);


        return Collections.emptySet();

    }

    public PhotoDTO getAlbumThumbnail(Long albumId){
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("Album not found in database."));
        Photo thumb = album.getThumbnailPhoto();
        return deviantArtService.getDeviationWithToken(thumb, album.getAuthor().getName());
    }

    private Album convertAlbum(AlbumDTO dto) {
        Provider provider = providerRepository.findById(dto.provider()).orElseThrow(() -> new RuntimeException("Album provider not found in database"));
        Optional<Author> optAuthor = authorRepository.findAuthorByNameAndProvider(dto.author(), dto.provider());
        PhotoDTO thumbDTO = dto.thumbnail();

        Photo thumbnail = null;
        if(thumbDTO.getId() != null) {
            Optional<Photo> optThumb = photoRepository.findById(dto.thumbnail().getId());

            if(optThumb.isPresent()) {
                thumbnail = optThumb.get();
            }
        } else {
            int tokenIndex = thumbDTO.getUrl().indexOf("?token");

            String thumbUrl = thumbDTO.getUrl()
                    .substring(0, tokenIndex != -1 ? tokenIndex : thumbDTO.getUrl().length());

             thumbnail =  photoRepository.save(new Photo(
                     thumbDTO.getCode(),
                     thumbDTO.getTitle(),
                     thumbUrl.substring(0, Math.min(thumbUrl.length(), 255)),
                     thumbDTO.getMature()
             ));
        }

        Author author;
        if(optAuthor.isEmpty()) {
            String authorId = authorService.save(new AuthorDTO(null, dto.author(), "", dto.provider()));
            author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found in database."));
        } else author = optAuthor.get();


        return new Album(
                dto.code(),
                dto.name(),
                dto.url(),
                dto.size(),
                thumbnail,
                author,
                provider
        );
    }

    public static AlbumDTO convertDTO(Album album) {
        return convertDTO(album, false);
    }


    private static AlbumDTO convertDTO(Album album, Boolean favorited) {
        return new AlbumDTO(
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
