package coms309.studyshare.messages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface MessageRepository extends JpaRepository<Message, UUID> {

}
