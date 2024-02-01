package be.wanna.Referencerback.service.album;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.AuthorDTO;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.entity.Album;
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

    private final FavoritesRepository favoritesRepository;

    private final ProviderRepository providerRepository;

    private final AuthorService authorService;

    private final AuthorRepository authorRepository;

    public List<AlbumDTO> getAuthorAlbums(String author, String provider){
        if (provider.equals("deviantart")) {
            return deviantArtService.findUserAlbums(author);
        }

        return Collections.emptyList();
    }

    public Set<Photo> listPhotos(String author, String albumId, int page, int limit, String provider, Integer maxThumbsize){
        if (provider.equals("deviantart")) {
            return deviantArtService.listAlbumPhotosByPage(albumId, author, page, Math.min(limit, 60), maxThumbsize);
        }

        return Collections.emptySet();

    }

    public Long favoriteAlbum(AlbumDTO dto, String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Favorites favorites = user.getFavorites();
        if(favorites==null){
            favorites = new Favorites();
        }

        Album album;
        Optional<Album> optAlbum = albumRepository.findByCodeAndAuthorAndProvider(dto.code(), dto.author(), dto.provider());

        if(optAlbum.isPresent()) album = optAlbum.get();
        else album = albumRepository.save(convertAlbum(dto));

        favorites.addAlbum(album);

        Favorites savedFavs = favoritesRepository.save(favorites);

        user.setFavorites(savedFavs);

        savedFavs.setUser(user);
        favoritesRepository.save(savedFavs);

        return album.getId();
    }
    public AlbumDTO unFavoriteAlbum(Long id, String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Album album = albumRepository.findById(id).orElseThrow(() -> new RuntimeException("Album not found on database."));

        Favorites favorites = user.getFavorites();
        if(favorites==null){
            favorites = new Favorites();
        }
        if(favorites.getAlbums()!=null) favorites.removeAlbum(album.getId());

        favoritesRepository.save(favorites);

        return convertDTO(album);
    }

    public AlbumDTO unFavoriteAlbum(String code, String authorName, String provider, String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Album album = albumRepository.findByCodeAndAuthorAndProvider(code, authorName, provider).orElseThrow(() -> new RuntimeException("Album not found in database."));

        Favorites favorites = user.getFavorites();
        if(favorites==null){
            favorites = new Favorites();
        }
        if(favorites.getAlbums()!=null) favorites.removeAlbum(album.getId());

        favoritesRepository.save(favorites);

        return convertDTO(album);
    }

    public List<AlbumDTO> getFavoritedAlbums(String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Favorites favorites = user.getFavorites();
        if(favorites == null) return Collections.emptyList();

        Set<Album> albums = favorites.getAlbums();
        if(albums == null || albums.isEmpty()) return Collections.emptyList();

        return albums.stream().map(this::convertDTO).collect(Collectors.toList());
    }

    private Album convertAlbum(AlbumDTO dto) {
        Provider provider = providerRepository.findById(dto.provider()).orElseThrow(() -> new RuntimeException("Album provider not found in database"));
        Optional<Author> optAuthor = authorRepository.findAuthorByNameAndProvider(dto.author(), dto.provider());
        PhotoDTO thumbDTO = dto.thumbnail();

        Photo thumbnail = null;
        if(thumbDTO.id() != null) {
            Optional<Photo> optThumb = photoRepository.findById(dto.thumbnail().id());

            if(optThumb.isPresent()) {
                thumbnail = optThumb.get();
            }
        } else {
            int tokenIndex = thumbDTO.url().indexOf("?token");

             thumbnail =  photoRepository.save(new Photo(
                     thumbDTO.code(),
                     thumbDTO.title(),
                     thumbDTO.url().substring(0, tokenIndex != -1 ? tokenIndex : thumbDTO.url().length())
                     )
             );
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
                thumbnail,
                author,
                provider
        );
    }

    private AlbumDTO convertDTO(Album album) {
        Photo thumb = album.getThumbnailPhoto();

        return new AlbumDTO(
                album.getId(),
                album.getCode(),
                album.getName(),
                album.getUrl(),
                deviantArtService.getDeviationWithToken(thumb, album.getAuthor().getName()),
                album.getAuthor().getName(),
                album.getProvider().getName(),
                album.getPhotos() != null ? album.getPhotos().size() : null
        );
    }

}
