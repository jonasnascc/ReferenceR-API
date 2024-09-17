package be.wanna.Referencerback.entity.collections;

import be.wanna.Referencerback.entity.album.Album;
import be.wanna.Referencerback.entity.album.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;

    @ManyToOne
    private UserCollection collection;

    @ManyToMany
    private Set<Photo> photos;

    @ManyToMany
    private Set<Album> albums;

    @PreRemove
    private void removeFromDependencies() {
        if(albums!=null) albums.clear();
        this.collection = null;
        if(photos!=null) photos.forEach(ph -> ph.removeCollectionLog(this.id));
    }

    public void addPhoto(Photo p) {
        if(photos == null) photos = new HashSet<>();
        if(photos.stream().noneMatch(ph -> ph.getCode().equals(p.getCode()))){
            photos.add(p);
        }
    }

    public void removePhoto(Long id) {
        if(photos!=null) photos.removeIf(ph -> ph.getId().equals(id));
    }

}
