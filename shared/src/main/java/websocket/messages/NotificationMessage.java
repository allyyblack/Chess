package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage{
    public NotificationMessage(ServerMessageType type, String message) {
        super(type, message);
    }

}
