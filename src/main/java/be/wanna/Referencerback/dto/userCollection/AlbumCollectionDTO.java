package be.wanna.Referencerback.dto.userCollection;

import java.util.List;

public record AlbumCollectionDTO (
        String albumCode,
        List<PhotoByPageDTO> photos,
        List<PhotoByPageDTO> exceptPhotos,
        boolean saveAsFavorite
){}
