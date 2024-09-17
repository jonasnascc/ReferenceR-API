package be.wanna.Referencerback.entity.album;

import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.photo.Photo;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapAlbum extends Album{
    private String code;

    private String url;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Provider provider;

    public ScrapAlbum(
                    String code,
                    String name,
                    String url,
                    Integer size,
                    Provider provider,
                    Author author) {
        super(name, url, size, provider);
        this.code = code;
        this.author = author;
    }

    public ScrapAlbum(
            String code,
            String name,
            String url,
            Integer size,
            Photo thumbnailPhoto,
            Author author,
            Provider provider
    ) {
        super(name, size, thumbnailPhoto, provider);
        this.code = code;
        this.url = url;
        this.author = author;
    }

}
