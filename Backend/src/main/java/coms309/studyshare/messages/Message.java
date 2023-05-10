package coms309.studyshare.messages;


import coms309.studyshare.channels.Channel;
import coms309.studyshare.users.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue
    @Getter
    private UUID ID;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private MessageType messageType;

    @Getter
    @Setter
    private String message;

    @ManyToOne(targetEntity = Channel.class)
    @JoinColumn(name = "channel_id")
    @Getter
    private Channel channel;

    //Could be Many to One but seems unnecessary in this case
    @Getter
    @Setter
    private UUID userID;

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("ID", ID);
        obj.put("messageType", messageType.name());
        obj.put("message", message);
        obj.put("channelID", channel.getID());
        return obj;

    }

    public static JSONArray listToJSONArray(List<Message> list) {
        JSONArray arr = new JSONArray();
        for (Message m : list) {
            arr.put(m.toJSON());
        }
        return arr;
    }
}
