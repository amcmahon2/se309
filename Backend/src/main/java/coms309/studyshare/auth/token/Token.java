package coms309.studyshare.auth.token;

import coms309.studyshare.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String token;

    public boolean revoked;
    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "token_id")
    public User user;
}
