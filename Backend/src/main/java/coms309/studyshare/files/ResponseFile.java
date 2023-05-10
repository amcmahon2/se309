package coms309.studyshare.files;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFile {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private long size;

}