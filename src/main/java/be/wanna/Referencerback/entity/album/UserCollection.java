package be.wanna.Referencerback.entity.album;

import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCollection extends Album{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CollectionLog> logs;

    @ManyToOne
    private User user;

    public UserCollection(String name, String description, User user) {
        super(name);
        this.description = description;
        this.user = user;
    }

    public void addLog(CollectionLog log) {
        if(logs == null) logs = new HashSet<>();
        logs.add(log);
    }

    @PreRemove
    private void removeFromDependencies(){
        this.user = null;
    }

    @Override
    public String toString() {
        return "UserCollection{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", logs=" + logs +
                ", user=" + user +
                "} " + super.toString();
    }
}
