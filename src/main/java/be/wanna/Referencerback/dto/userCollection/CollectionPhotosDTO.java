package be.wanna.Referencerback.dto.userCollection;

import java.util.List;

public record CollectionPhotosDTO(
        List<AlbumCollectionDTO> albums
) {
}
