package be.wanna.Referencerback.entity.album;

import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.photo.Photo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer size;

    @ManyToOne
    private Photo thumbnailPhoto;

    @ManyToMany
    @JsonIgnore
    private Set<Photo> photos;


    public Album(String name) {
        this.name = name;
    }

    public Album(String name, Integer size, Photo thumbnailPhoto) {
        this.name = name;
        this.size = size;
        this.thumbnailPhoto = thumbnailPhoto;
    }

    public Album(String name, String url, Integer size) {
        this.name = name;
        this.size = size;
    }

    public void removePhoto(Long id) {
        if(photos!=null) photos.removeIf(ph -> ph.getId().equals(id));
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", thumbnailPhoto=" + thumbnailPhoto +
                ", photos=" + photos +
                '}';
    }
}
