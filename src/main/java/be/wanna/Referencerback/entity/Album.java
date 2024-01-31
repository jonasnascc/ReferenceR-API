package be.wanna.Referencerback.entity;

import be.wanna.Referencerback.entity.photo.Photo;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String url;

    private String thumbnailUrl;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Provider provider;

    @ManyToMany
    @JoinTable(name="albums_photos", joinColumns = {@JoinColumn(name="album_id")}, inverseJoinColumns = {@JoinColumn(name="photo_id")})
    private Set<Photo> photos;

    public Album(String code, String name, String url, String thumbnailUrl) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Album(String code, String name, String url, String thumbnailUrl, Author author, Provider provider) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.author = author;
        this.provider = provider;
    }

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
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", author=" + author +
                ", provider=" + provider +
                ", photos=" + photos +
                '}';
    }
}
