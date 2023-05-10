package coms309.studyshare.channels;

import coms309.studyshare.courses.Course;
import coms309.studyshare.messages.Message;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel")
public class Channel {


    @Id
    @GeneratedValue
    @Getter
    private UUID ID;

    @Getter
    @Setter
    private String name;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "course_id")
//    @Getter
//    @Setter
//    private Course course;

    @Getter
    @Setter
    private boolean hidden;


    @OneToMany(targetEntity=Message.class,cascade = CascadeType.ALL , fetch = FetchType.EAGER, mappedBy = "channel")
    @Getter
    @Setter
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message m) {
        this.messages.add(m);
        m.setChannel(this);
    }

}
