package coms309.studyshare.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    @Autowired
    private FileDataRepository fileRepo;

    public FileData store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileData FileDB = new FileData(null, fileName, file.getContentType(), file.getBytes());

        return fileRepo.save(FileDB);
    }

    public FileData getFile(UUID id) {
        return fileRepo.findById(id).get();
    }

    public Stream<FileData> getAllFiles() {
        return fileRepo.findAll().stream();
    }
}
