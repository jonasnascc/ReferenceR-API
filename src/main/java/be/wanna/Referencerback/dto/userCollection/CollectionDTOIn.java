package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.PhotoDTO;

import java.util.List;
import java.util.Set;

public record CollectionDTOIn(
        String name,
        String description,
        List<AlbumCollectionDTO> albums
){
}
