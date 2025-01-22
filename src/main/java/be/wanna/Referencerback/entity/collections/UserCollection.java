package be.wanna.Referencerback.entity.collections;

import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CollectionLog> logs;

    @ManyToOne
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    private Set<Photo> photos;

    public UserCollection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addPhoto(Photo p) {
        if(photos == null) photos = new HashSet<>();
        if(photos.stream().noneMatch(ph -> ph.getCode().equals(p.getCode()))){
            photos.add(p);
        }
    }

    public void addLog(CollectionLog log) {
        if(logs == null) logs = new HashSet<>();
        logs.add(log);
    }


    public void removePhoto(Long id) {
        if(photos!=null) photos.removeIf(ph -> ph.getId().equals(id));
    }

    @PreRemove
    private void removeFromDependencies(){
        this.user = null;
        if(photos!=null) {
            photos.forEach(photo -> photo.removeCollection(this.id));
        }

    }
}
