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

    private final DeviantArtService deviantArtService;

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
}
