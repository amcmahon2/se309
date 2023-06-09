package coms309.studyshare.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Integer> {


    @Query(value = "select t from Token t inner join User u on t.user.id = u.id where u.id = :id and (t.expired = false and t.revoked = false)")
    List<Token> findAllValidTokenByUser(UUID id);

    Optional<Token> findByToken(String token);
}
