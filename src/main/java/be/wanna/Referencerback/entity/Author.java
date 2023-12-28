package be.wanna.Referencerback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private String name;

    private String profileUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private Set<Album> albums;

    public Author(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
    }
}
