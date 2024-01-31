package be.wanna.Referencerback.dto;

public record AlbumDTO (
        Long id,
        String code,
        String name,
        String url,
        String thumbUrl,
        String author,
        String provider,
        Integer size
){}
