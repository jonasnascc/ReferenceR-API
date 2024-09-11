package be.wanna.Referencerback.entity;

import be.wanna.Referencerback.entity.connection.Connection;
import be.wanna.Referencerback.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Provider {
    @Id
    private String name;

    private String url;

    private String loginUrl;

    private String loginPath;

    @OneToMany(mappedBy = "provider")
    private List<Connection> connections;

    @OneToMany(mappedBy = "provider")
    private List<Album> albums;

    @OneToMany(mappedBy = "provider")
    private List<Author> authors;

    @ManyToOne
    private User user;

    public Provider(String name) {
        this.name = name;
    }

    public Provider(String name, String url, String loginUrl, String loginPath, User user) {
        this.name = name;
        this.url = url;
        this.loginUrl = loginUrl;
        this.loginPath = loginPath;
        this.user = user;
    }

    public Provider(String name, String url, String loginUrl, String loginPath) {
        this.name = name;
        this.url = url;
        this.loginUrl = loginUrl;
        this.loginPath = loginPath;
    }
}
