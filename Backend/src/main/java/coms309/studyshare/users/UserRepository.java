package coms309.studyshare.users;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByID(UUID id);

    Optional<User> findByNetID(String netID);

    User findByNetIDIgnoreCase(String netID);

    @Transactional
    void deleteByID(UUID id);
}
