package be.wanna.Referencerback.service.photo;

import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.dto.deviantArt.TagDTO;
import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.Favorites;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.FavoritesRepository;
import be.wanna.Referencerback.repository.PhotoRepository;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final DeviantArtService deviantArtService;

    private final PhotoRepository photoRepository;

    private final UserRepository userRepository;

    private final FavoritesRepository favoritesRepository;

    public Long save(Photo photo){

        return photoRepository.save(photo).getId();
    }

    public String getDeviationInfoByUrl(String url){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("url", url);

            Connection.Response response = Jsoup.connect("https://backend.deviantart.com/oembed")
                    .data(data)
                    .ignoreContentType(true)
                    .execute();

            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TagDTO> getTagsByUrl(String url) {
        return deviantArtService.getDeviationTagsByUrl(url);
    }

    public Long favoritePhoto(PhotoDTO dto, Long albumId, String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Photo photo = photoRepository.findPhotoByCode(dto.code())
                .orElse(photoRepository.save(new Photo(dto.code(), dto.title(), dto.url(), dto.mature())));

        Favorites favorites = user.getFavorites();
        if(favorites == null) favorites = new Favorites();

        favorites.addPhoto(photo);

        favoritesRepository.save(favorites);

        return photo.getId();
    }

    public PhotoDTO unfavoritePhoto(Long id, String code, Long albumId, String login) {
        User user = userRepository.findByLogin(login);
        if(user == null) throw new RuntimeException("User not found in database.");

        Photo photo = null;
        if(id!=null) {
            photo = photoRepository.findById(id).orElseThrow(() -> new RuntimeException("Photo not found in database."));
        }
        else if(code != null){
            photo = photoRepository.findPhotoByCode(code).orElseThrow(() -> new RuntimeException("Photo not found in database."));
        }
        if(photo == null) throw new RuntimeException("Photo identification is invalid.");

        Favorites favorites = user.getFavorites();
        if(favorites == null) favorites = new Favorites();

        favorites.removePhoto(photo.getId());

        favoritesRepository.save(favorites);

        return new PhotoDTO(photo.getId(), photo.getCode(), photo.getUrl(), photo.getTitle(), photo.isMature());
    }
}
