package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.photo.PhotoDTO;

import java.util.Set;

public record CollectionPhotosPageDTO (
        Integer page,
        Boolean hasMore,
        Set<PhotoDTO> photos
){}
