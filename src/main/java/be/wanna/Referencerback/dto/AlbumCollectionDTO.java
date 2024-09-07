package be.wanna.Referencerback.dto;

import java.util.List;

public record AlbumCollectionDTO (
        String albumCode,
        List<String> exceptPhotos,

        boolean saveAsFavorite
){}
