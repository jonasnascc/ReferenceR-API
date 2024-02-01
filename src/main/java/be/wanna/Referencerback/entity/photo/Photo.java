package be.wanna.Referencerback.entity.photo;

import be.wanna.Referencerback.entity.Album;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    private boolean mature;

    private PhotoType type;

    private String thumbUrl;

    private String matureLevel;

    private String photoPage;

    private String license;

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<Album> albums;

    public Photo(String code, String title, String url) {
        this.code = code;
        this.title = title;
        this.url = url;
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

}