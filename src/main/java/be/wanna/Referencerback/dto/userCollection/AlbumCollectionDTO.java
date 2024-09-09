package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.AlbumDTO;

import java.util.List;

public record AlbumCollectionDTO (
        AlbumDTO album,
        List<UserCollectionPhotoDTO> photos,
        List<UserCollectionPhotoDTO> exceptPhotos,
        boolean saveAsFavorite
){}
