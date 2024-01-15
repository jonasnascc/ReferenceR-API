package be.wanna.Referencerback.service;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.ConnectionDTO;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AlbumsService {

    private final UserRepository userRepository;
    public List<AlbumDTO> getAuthorAlbums(String author, String provider){
        if (provider.equals("deviantart")) {
            return DeviantArtService.findUserAlbums(author);
        }

        return Collections.emptyList();
    }

    public Set<Photo> listPhotos(String author, String albumId, int page, int limit, String provider){
        if (provider.equals("deviantart")) {
            return DeviantArtService.listAlbumPhotosByPage(albumId, author, page, Math.min(limit, 60));
        }

        return Collections.emptySet();

    }
}
