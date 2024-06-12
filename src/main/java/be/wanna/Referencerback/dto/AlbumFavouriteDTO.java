package be.wanna.Referencerback.dto;

import java.util.List;

public record AlbumFavouriteDTO(
        AlbumDTO album,
        List<String> except
) {
}
