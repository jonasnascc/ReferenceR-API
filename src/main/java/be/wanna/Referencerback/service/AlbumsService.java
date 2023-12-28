package be.wanna.Referencerback.service;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AlbumsService {
    public List<AlbumDTO> getAuthorAlbums(String author, String provider){
        if (provider.equals("deviantart")) {
            return DeviantArtService.findUserAlbums(author);
        }
        System.out.printf(provider);
        return Collections.emptyList();
    }
}
