package be.wanna.Referencerback.entity.photo;

import be.wanna.Referencerback.entity.Album;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
    private Long id;

    private String title;

    private String url;

    private boolean mature;

    private PhotoType type;

    @ManyToMany(mappedBy = "photos")
    @JsonIgnore
    private Set<Album> albums;

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