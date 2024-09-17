package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.album.ScrapAlbumDTO;
import be.wanna.Referencerback.entity.photo.Photo;

import java.util.List;

public record AlbumCollectionDTOOut(
        ScrapAlbumDTO album,

        List<Photo> photos
){}
