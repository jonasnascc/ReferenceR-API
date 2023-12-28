package be.wanna.Referencerback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    @Id
    private String id;

    private String name;

    private String url;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Provider provider;

    private Integer lastOffset;

    @ManyToMany
    @JoinTable(name="albums_photos", joinColumns = {@JoinColumn(name="album_id")}, inverseJoinColumns = {@JoinColumn(name="photo_id")})
    private Set<Photo> photos;

    public void addPhoto(Photo photo){
        if(photos == null) photos = new HashSet<>();
        for(Photo p : photos){
            if(p.getId().equals(photo.getId())) return;
        }
        photos.add(photo);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", author=" + author +
                '}';
    }
}
