package be.wanna.Referencerback.service;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.ConnectionDTO;
import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.Photo;
import be.wanna.Referencerback.entity.User;
import be.wanna.Referencerback.repository.UserRepository;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public Set<Photo> listPhotos(String author, String albumId, int page, int limit, String provider, ConnectionDTO userDTO){
        Optional<User> optUser = userRepository.findById(userDTO.username());
        if(optUser.isEmpty()) throw new RuntimeException("User not found.");
        User user = optUser.get();
        if(!Integer.toString(userDTO.password().hashCode()).equals(user.getPassword())){
            throw new RuntimeException("User not authorized.");
        }

        if (provider.equals("deviantart")) {
            return DeviantArtService.listAlbumPhotosByPage(albumId, author, page, Math.min(limit, 60), user);
        }

        return Collections.emptySet();

    }
}
