package coms309.studyshare.files;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
public class FileData {

    @Id
    @GeneratedValue
    @Getter
    private UUID ID;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String type;

    @Lob
    @Getter
    @Setter
    private byte[] data;


}
