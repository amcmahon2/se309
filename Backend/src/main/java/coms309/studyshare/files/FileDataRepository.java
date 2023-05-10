package coms309.studyshare.files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileDataRepository extends JpaRepository<FileData, UUID> {
}
