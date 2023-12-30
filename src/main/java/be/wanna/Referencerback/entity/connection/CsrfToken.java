package be.wanna.Referencerback.entity.connection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
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
public class CsrfToken {
    @Id
    private String token;


    @OneToMany(mappedBy = "csrfToken")
    private List<Cookie> cookies;

    public CsrfToken(String csrfToken) {
        this.token = csrfToken;
    }

    @Override
    public String toString() {
        return token;
    }
}
