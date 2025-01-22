package be.wanna.Referencerback.entity;

import be.wanna.Referencerback.entity.album.Album;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String profileUrl;

    @ManyToOne
    private Provider provider;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private Set<Album> albums;

    public Author(String name) {
        this.name = name;
    }

    public Author(String name, String profileUrl, Provider provider) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.provider = provider;
    }
}
