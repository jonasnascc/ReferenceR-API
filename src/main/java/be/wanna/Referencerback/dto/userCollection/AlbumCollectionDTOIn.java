package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.album.ScrapAlbumDTO;
import be.wanna.Referencerback.dto.PhotoDTO;

import java.util.List;

public record AlbumCollectionDTOIn(
        ScrapAlbumDTO album,
        List<PhotoDTO> photos,
        List<PhotoDTO> exceptPhotos,
        boolean saveAsFavorite
){}
