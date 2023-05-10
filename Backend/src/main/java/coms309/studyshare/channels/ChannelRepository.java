package coms309.studyshare.channels;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Channel findByID(UUID id);
    Channel findByName(String name);
}
