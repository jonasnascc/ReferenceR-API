package be.wanna.Referencerback.dto.userCollection;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.entity.photo.Photo;

import java.util.List;

public record AlbumCollectionDTOOut(
        AlbumDTO album,

        List<Photo> photos
){}
