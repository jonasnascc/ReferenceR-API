package be.wanna.Referencerback.entity.connection;

import be.wanna.Referencerback.entity.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Provider provider;

    @OneToMany
    @JoinTable(name="dvnt_art_cookies_connections",
            joinColumns = {@JoinColumn(name="connection_id")},
            inverseJoinColumns = {@JoinColumn(name="cookie_id")}
    )
    private List<Cookie> cookies;

    private boolean remember;
}
