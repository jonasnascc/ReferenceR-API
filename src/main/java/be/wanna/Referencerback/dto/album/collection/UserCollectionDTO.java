package be.wanna.Referencerback.dto.album.collection;

import be.wanna.Referencerback.dto.PhotoDTO;

public record UserCollectionDTO(
        Long id,
        String name,
        String description,
        PhotoDTO thumbnail,
        Integer size
){
    public UserCollectionDTO(Long id, String name, String description, Integer size) {
        this(id, name, description, null, size);
    }
}
