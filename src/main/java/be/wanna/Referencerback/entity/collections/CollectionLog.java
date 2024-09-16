package be.wanna.Referencerback.entity.collections;

import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
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

    @ManyToOne(cascade = CascadeType.ALL)
    private UserCollection collection;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Photo> photos;

    @ManyToMany
    private Set<Album> albums;

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
