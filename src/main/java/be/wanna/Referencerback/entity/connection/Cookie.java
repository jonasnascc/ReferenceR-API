package be.wanna.Referencerback.entity.connection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {
    @Id
    private String id;

    private String content;

    @ManyToOne
    private CsrfToken csrfToken;

    @ManyToOne
    private Connection connection;

    public Cookie(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", connection=" + connection +
                '}';
    }
}
