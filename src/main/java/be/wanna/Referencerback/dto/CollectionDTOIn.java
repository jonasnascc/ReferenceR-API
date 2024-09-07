package be.wanna.Referencerback.dto;

import java.util.Set;

public record CollectionDTOIn(
        String name,
        String description,

        Set<PhotoDTO> photos,

        AlbumCollectionDTO album
){}
