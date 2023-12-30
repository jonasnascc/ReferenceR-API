package be.wanna.Referencerback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_RFR")
public class User{
    @Id
    private String username;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<Provider> providers;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}