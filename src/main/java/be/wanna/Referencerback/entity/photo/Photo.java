package be.wanna.Referencerback.entity.photo;

import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.UserCollection;
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

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<Album> albums;

    @ManyToMany
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

    public Photo(String code, String title, String url, Boolean mature, Integer page) {
        this.code = code;
        this.title = title;
        this.url = url;
        this.mature = mature;
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

}