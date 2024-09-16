package be.wanna.Referencerback.entity.photo;

import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.collections.UserCollection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private String title;

    private String url;

    private Boolean mature;

    private PhotoType type;

    private String thumbUrl;

    private String matureLevel;

    private String photoPage;

    private String license;

    private Date publishedTime;

    private Integer page;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Author author;

    @ManyToMany(mappedBy = "photos", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<CollectionLog> collectionLogs;

    @ManyToMany(mappedBy = "photos", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Album> albums;

    @ManyToMany(mappedBy = "photos", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<UserCollection> collections;

    public Photo(String code, Integer page) {
        this.code = code;
        this.page = page;
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
                 Boolean mature,
                 PhotoType type,
                 String thumbUrl,
                 String matureLevel,
                 String photoPage,
                 String license,
                 Date publishedTime,
                 Integer page) {
        this.code = code;
        this.title = title;
        this.url = url;
        this.mature = mature;
        this.type = type;
        this.thumbUrl = thumbUrl;
        this.matureLevel = matureLevel;
        this.photoPage = photoPage;
        this.license = license;
        this.publishedTime = publishedTime;
        this.page = page;
    }

    public Photo(String code, String title, String url, String photoPage) {
        this.code = code;
        this.title = title;
        this.url = url;
        this.photoPage = photoPage;
    }



    public void addAlbum(Album album){
        if(albums == null) albums = new HashSet<>();
        for(Album a: albums){
            if(a.getId().equals(album.getId())){
                return;
            }
        }
        albums.add(album);
    }

    public void addCollection(UserCollection collection) {
        if(collections == null) collections = new HashSet<>();
        if(collections.stream().noneMatch(col -> collection.getId().equals(col.getId()))){
            collections.add(collection);
        }
    }

    @PreRemove
    private void removeFromDependencies (){
        if(collections!=null) collections.forEach(col -> col.removePhoto(this.getId()));
        this.author = null;
        if(albums!=null) albums.forEach(album -> album.removePhoto(this.getId()));
        if(collectionLogs!=null) collectionLogs.forEach(log -> log.removePhoto(this.getId()));
    }

    public void removeCollection(Long id) {
        if(collections!=null) collections.removeIf(col -> col.getId().equals(id));

    }
}