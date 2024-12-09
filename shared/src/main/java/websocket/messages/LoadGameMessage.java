package websocket.messages;

import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    Integer game;
    public LoadGameMessage(ServerMessageType type, Integer game) {
        super(type);
        this.game = game;
    }
    @Override
    public String getMessage() {
        return "Game Loaded";
    }
}
