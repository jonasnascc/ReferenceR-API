package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.PhotoDTO;

import java.util.Set;

public record CollectionDTOIn(
        String name,
        String description,

        Set<PhotoDTO> photos,

        AlbumCollectionDTO album
){
    public CollectionDTOIn(
            String name,
            String description,
            Set<PhotoDTO> photos
    ){
        this(name, description, photos, null);
    }
}
