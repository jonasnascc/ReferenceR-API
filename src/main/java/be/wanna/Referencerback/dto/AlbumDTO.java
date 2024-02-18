package be.wanna.Referencerback.dto;

public record AlbumDTO (
        Long id,
        String code,
        String name,
        String url,
        PhotoDTO thumbnail,
        String author,
        String provider,
        Integer size,
        boolean favorited
){}
