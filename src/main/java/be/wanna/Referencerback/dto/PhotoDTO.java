package be.wanna.Referencerback.dto;

import be.wanna.Referencerback.entity.photo.PhotoType;

import java.util.Date;

public record PhotoDTO (
    Long id,
    String code,
    String url,
    String title,
    Boolean mature,
    PhotoType type,
    String thumbUrl,
    String matureLevel,
    String photoPage,
    String license,
    Date publishedTime,

    Integer page
) {
    public PhotoDTO (
            Long id,
            String code,
            String url,
            String title,
            Boolean mature
    ){
        this(id,
                code,
                url,
                title,
                mature,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
