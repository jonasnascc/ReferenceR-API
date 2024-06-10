package be.wanna.Referencerback.entity.token;

import be.wanna.Referencerback.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private boolean expired;

    private boolean revoked;

    @ManyToOne
    private User user;

    public Token(String token, TokenType type, boolean expired, boolean revoked, User user) {
        this.token = token;
        this.type = type;
        this.expired = expired;
        this.revoked = revoked;
        this.user = user;
    }
}
