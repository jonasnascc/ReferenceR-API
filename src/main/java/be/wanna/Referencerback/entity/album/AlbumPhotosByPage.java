package be.wanna.Referencerback.entity.album;

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
public class AlbumPhotosByPage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer page;

    @ManyToOne
    private Album album;

    @ManyToMany
    private Set<Photo> photos;

    public void removePhoto(Long id) {
        if(photos!=null) photos.removeIf(ph -> ph.getId().equals(id));
    }

    public AlbumPhotosByPage(Integer page, Album album) {
        this.page = page;
        this.album = album;
    }

    public AlbumPhotosByPage(Integer page, Album album, Set<Photo> photos) {
        this.page = page;
        this.album = album;
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        if(photos==null) photos = new HashSet<>();
        if(photos.stream().noneMatch(ph -> ph.getId().equals(photo.getId()))) {
            photos.add(photo);
        }
    }
}
