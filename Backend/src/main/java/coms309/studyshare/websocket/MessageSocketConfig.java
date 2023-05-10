package coms309.studyshare.websocket;

import coms309.studyshare.users.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSocket
public class MessageSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)  {
        registry.addHandler(messageHandler(), "/chat/*").addInterceptors(messageIntercepter());
    }

    @Bean
    public HandshakeInterceptor messageIntercepter() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                String path = request.getURI().getPath();
                UUID uuid = UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
                attributes.put("channelID", uuid);
                attributes.put("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    @Bean
    public WebSocketHandler messageHandler() {
        return new MessageHandler();
    }
}
