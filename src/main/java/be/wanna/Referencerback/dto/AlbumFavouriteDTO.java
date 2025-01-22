package be.wanna.Referencerback.dto;

import be.wanna.Referencerback.dto.album.AlbumDTO;

import java.util.List;

public record AlbumFavouriteDTO(
        AlbumDTO album,
        List<String> except
) {
}
