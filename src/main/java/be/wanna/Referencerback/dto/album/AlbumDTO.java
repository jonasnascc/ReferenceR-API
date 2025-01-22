package be.wanna.Referencerback.dto.album;

import be.wanna.Referencerback.dto.photo.PhotoDTO;

public record AlbumDTO (
        Long id,
        String code,
        String name,
        String url,
        PhotoDTO thumbnail,
        String author,
        String provider,
        Integer size
){}
