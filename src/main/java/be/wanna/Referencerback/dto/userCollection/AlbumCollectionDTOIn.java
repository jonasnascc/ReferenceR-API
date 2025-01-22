package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.album.AlbumDTO;
import be.wanna.Referencerback.dto.photo.PhotoDTO;

import java.util.List;

public record AlbumCollectionDTOIn(
        AlbumDTO album,
        List<PhotoDTO> photos,
        List<PhotoDTO> exceptPhotos,
        boolean saveAsFavorite
){}
