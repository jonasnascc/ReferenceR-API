package be.wanna.Referencerback.dto.userCollection;

import java.util.List;

public record CollectionDTOIn(
        String name,
        String description,
        List<AlbumCollectionDTOIn> albums
){
}
