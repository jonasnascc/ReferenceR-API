package be.wanna.Referencerback.entity;

import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
