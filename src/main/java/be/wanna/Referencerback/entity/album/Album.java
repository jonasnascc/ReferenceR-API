package be.wanna.Referencerback.entity.album;

import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
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

    private Integer size;

    @ManyToOne
    private Photo thumbnailPhoto;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Provider provider;

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

    public Album(String code, Author author, Provider provider) {
        this.code = code;
        this.author = author;
        this.provider = provider;
    }

    public Album(String code, String name, String url, Integer size, Photo thumbnailPhoto, Author author, Provider provider) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.size = size;
        this.thumbnailPhoto = thumbnailPhoto;
        this.author = author;
        this.provider = provider;
    }

    public Album(String code, String name, String url, Integer size, Author author, Provider provider) {
        this.code = code;
        this.name = name;
        this.url = url;
        this.size = size;
        this.author = author;
        this.provider = provider;
    }


    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", author=" + author +
                ", provider=" + provider +
                ", photos=" + photos +
                '}';
    }
}
