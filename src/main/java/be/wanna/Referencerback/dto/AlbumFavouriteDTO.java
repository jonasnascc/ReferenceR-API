package be.wanna.Referencerback.dto;

import be.wanna.Referencerback.dto.album.ScrapAlbumDTO;

import java.util.List;

public record AlbumFavouriteDTO(
        ScrapAlbumDTO album,
        List<String> except
) {
}
