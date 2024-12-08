package websocket.messages;

import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    Integer game;
    public LoadGameMessage(ServerMessageType type, String message, Integer game) {
        super(type, message);
        this.game = game;
    }
    public void NullMessage() {
        this.message = null;
    }
}
