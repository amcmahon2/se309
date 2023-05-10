package coms309.studyshare.websocket;

import coms309.studyshare.channels.Channel;
import coms309.studyshare.channels.ChannelRepository;
import coms309.studyshare.messages.Message;
import coms309.studyshare.messages.MessageRepository;
import coms309.studyshare.messages.MessageType;
import coms309.studyshare.users.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessageHandler extends TextWebSocketHandler {


    public static HashMap<Channel, ArrayList<WebSocketSession>> channelSessionMap = new HashMap<>();

    private User user;

    private Channel channel;

    private static MessageRepository messageRepository;


    private static ChannelRepository channelRepository;

    @Autowired
    public void setMessageRepository(MessageRepository repo) {
        messageRepository = repo;
    }

    @Autowired
    public void setChannelRepository(ChannelRepository repo) {
        channelRepository = repo;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        user = (User) session.getAttributes().get("user");
        channel = channelRepository.findByID((UUID) session.getAttributes().get("channelID"));
        List<Message> messageList = channel.getMessages();
        session.sendMessage(new TextMessage(Message.listToJSONArray(messageList).toString()));

        ArrayList<WebSocketSession> sessionList;
        if (channelSessionMap.containsKey(channel)) {
            sessionList = channelSessionMap.get(channel);
            sessionList.add(session);
            channelSessionMap.put(channel, sessionList);
        } else {
            sessionList = new ArrayList<>();
            sessionList.add(session);
            channelSessionMap.put(channel, sessionList);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        JSONObject obj = new JSONObject(message);
        obj = new JSONObject(obj.getString("payload"));
        String msgText = obj.getString("message");
        MessageType type = MessageType.valueOf(obj.getString("messageType"));
        Message msg = new Message(null, type, msgText, channel, user.getID());
        messageRepository.save(msg);
        channel.addMessage(msg);
        channelRepository.save(channel);
        sendToChannel(new TextMessage(msg.toJSON().toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        ArrayList<WebSocketSession> sessionList = channelSessionMap.get(channel);
        sessionList.remove(session);
        channelSessionMap.put(channel, sessionList);
    }


    public void sendToChannel(TextMessage textMessage) throws IOException {

        for (WebSocketSession session : channelSessionMap.get(channel)) {
            session.sendMessage(textMessage);
        }
    }


}
