package be.wanna.Referencerback.dto;

public record AlbumDTO (
        String code,
        String name,
        String url,
        String author,
        String provider,
        Integer photosQuantity
){}
