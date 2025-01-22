package be.wanna.Referencerback.dto.photo;

import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.album.AlbumPhotosByPage;
import be.wanna.Referencerback.entity.photo.PhotoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private Long id;

    private String code;

    private String url;

    private String token;

    private Date tokenExpireTime;

    private String authorName;

    private String title;

    private Boolean mature;

    private PhotoType type;

    private String thumbUrl;

    private String matureLevel;

    private String webPage;

    private String license;

    private Date publishedTime;

    private List<PhotoAlbumByPageDTO> photoAlbumByPage;

    public PhotoDTO(Long id, String code, String url, String title, Boolean mature) {
        this.id = id;
        this.code = code;
        this.url = url;
        this.title = title;
        this.mature = mature;
    }

    public PhotoDTO(
            Long id,
            String code,
            String title,
            String url,
            String token,
            Date tokenExpireTime,
            Author author,
            String webPage,
            Boolean mature,
            PhotoType type,
            String thumbUrl,
            String matureLevel,
            String license,
            Date publishedTime,
            Set<AlbumPhotosByPage> albumPhotosByPage
    ){
        this.id = id;
        this.code = code;
        this.title = title;
        this.url = url;
        this.token = token;
        this.tokenExpireTime = tokenExpireTime;
        this.authorName = author.getName();
        this.webPage = webPage;
        this.mature = mature;
        this.type = type;
        this.thumbUrl = thumbUrl;
        this.matureLevel = matureLevel;
        this.license = license;
        this.publishedTime = publishedTime;
        this.photoAlbumByPage = albumPhotosByPage != null ? albumPhotosByPage.stream()
                .map(p -> new PhotoAlbumByPageDTO(p.getId(), p.getPage(), p.getAlbum().getId()))
                .collect(Collectors.toList()) : null;
    }
}
