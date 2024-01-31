package be.wanna.Referencerback.entity.user;

import be.wanna.Referencerback.entity.Album;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "favorites")
    private User user;

    @ManyToMany
    private Set<Album> albums;

    public void addAlbum(Album album){
        if(albums == null) albums = new HashSet<>();
        if(albums.stream().noneMatch(alb -> alb.getId().equals(album.getId()))){
            albums.add(album);
        }

    }

    public void removeAlbum(Long id){
        if(albums != null) {
            albums.removeIf(album -> album.getId().equals(id));
        }
    }
}
