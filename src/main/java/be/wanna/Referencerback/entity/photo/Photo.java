package be.wanna.Referencerback.entity.photo;

import be.wanna.Referencerback.entity.album.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.album.AlbumPhotosByPage;
import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.collections.UserCollection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private String title;

    private String url;

    private String webPage;

    private Boolean mature;

    private PhotoType type;

    private String thumbUrl;

    private String matureLevel;

    private String license;

    private Date publishedTime;

    @Lob
    private String token;

    private Date tokenExpireTime;

    @ManyToOne
    private Author author;

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<CollectionLog> collectionLogs;

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<AlbumPhotosByPage> albumPhotosByPages;

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<UserCollection> collections;

    public Photo(String code, Integer page) {
        this.code = code;
    }

    public Photo(String code, String title, String url, Boolean mature) {
        this.code = code;
        this.title = title;
        this.url = url;
        this.mature = mature;
    }

    public Photo(String code,
                 String title,
                 String url,
                 String webPage,
                 Boolean mature,
                 PhotoType type,
                 String thumbUrl,
                 String matureLevel,
                 String license,
                 Date publishedTime
                ) {
        this.code = code;
        this.title = title;
        this.url = url;
        this.mature = mature;
        this.type = type;
        this.thumbUrl = thumbUrl;
        this.matureLevel = matureLevel;
        this.license = license;
        this.publishedTime = publishedTime;
        this.webPage = webPage;
    }

    public Photo(String code, String title, String url, Integer photoPage) {
        this.code = code;
        this.title = title;
        this.url = url;
    }



    public void addPhotoAlbumPage(AlbumPhotosByPage album){
        if(albumPhotosByPages == null) albumPhotosByPages = new HashSet<>();
        for(AlbumPhotosByPage a: albumPhotosByPages){
            if(a.getId().equals(album.getId())){
                return;
            }
        }
        albumPhotosByPages.add(album);
    }

    public void addCollection(UserCollection collection) {
        if(collections == null) collections = new HashSet<>();
        if(collections.stream().noneMatch(col -> collection.getId().equals(col.getId()))){
            collections.add(collection);
        }
    }

    public Set<Album> getAlbums() {
        return albumPhotosByPages.stream().map(AlbumPhotosByPage::getAlbum).collect(Collectors.toSet());
    }

    @PreRemove
    private void removeFromDependencies (){
        if(collections!=null) collections.forEach(col -> col.removePhoto(this.getId()));
        this.author = null;
        if(albumPhotosByPages !=null) albumPhotosByPages.forEach(album -> album.removePhoto(this.getId()));
        if(collectionLogs!=null) collectionLogs.forEach(log -> log.removePhoto(this.getId()));
    }

    public void removeCollection(Long id) {
        if(collections!=null) collections.removeIf(col -> col.getId().equals(id));

    }

    public void removeCollectionLog(Long id) {
        if(collectionLogs!=null) collectionLogs.removeIf(col -> col.getId().equals(id));
    }
}