package be.wanna.Referencerback.entity;

import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    private User user;

    @ManyToMany(mappedBy = "collections")
    private Set<Photo> photos;

    public UserCollection(String name, String description, Set<Photo> photos) {
        this.name = name;
        this.description = description;
        this.photos = photos;
    }

    public UserCollection(Long id, String name, String description, Set<Photo> photos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photos = photos;
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
