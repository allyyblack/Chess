package dataaccess;

import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;
import java.util.*;

public class MemoryDataAccess implements DataAccess{
    final private Collection<UserData> users = new HashSet<>();
    final private Collection<AuthData> tokens = new HashSet<>();
    final private Collection<GameData> games = new HashSet<>();
    private int nextId = 1;




    @Override
    public void clear() {
        users.clear();
        tokens.clear();
        games.clear();
    }

    @Override
    public UserData createUser(UserData userData) {
        users.add(userData);
        return userData;
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String getAuthToken(String username) throws DataAccessException {
        return "";
    }

    @Override
    public boolean isGameValid(int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public boolean isAuthTokenValid(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public GameData createGame(String gameName) {
        ChessGame game = new ChessGame();
        GameData data = new GameData(nextId++, null, null, gameName, game);
        games.add(data);
        return data;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : games) {
            if (Objects.equals(gameData.gameID(), gameID)) {
                return gameData;
            }
        }
        throw new DataAccessException("Game not found for ID: " + gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games;
    }

    @Override
    public void updateGame(int gameID, String authToken, String color) throws DataAccessException {
        GameData gameData = getGame(gameID);
        if (authToken == null) {
            throw new DataAccessException("authtoken null");
        }
        AuthData authData = getAuth(authToken);
        if (authData != null) {
            if (Objects.equals(color, "WHITE")) {
                GameData updatedGame = new GameData(gameID, authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                games.remove(gameData);
                games.add(updatedGame);
            }
            if (Objects.equals(color, "BLACK")) {
                GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                games.remove(gameData);
                games.add(updatedGame);
            }
        }
    }

    @Override
    public AuthData createAuth(String username) {
        String token =  UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        tokens.add(authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("authtoken null");
        }
        for (AuthData authData : tokens) {
            if (Objects.equals(authData.authToken(), authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public GameData makeMove(ChessMove move, int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean isInCheck(ChessGame game, ChessGame.TeamColor color) {
        return false;
    }

    @Override
    public boolean isInCheckmate(ChessGame game, ChessGame.TeamColor color) {
        return false;
    }

    @Override
    public void updateGameState(int gameID, ChessGame chessGame) throws DataAccessException {

    }

    @Override
    public void deleteAuth(AuthData authToken) {
        tokens.remove(authToken);
    }

    @Override
    public void removeUser(int id, String color) throws DataAccessException {

    }

    @Override
    public void endGame(int gameID) throws DataAccessException {

    }

    @Override
    public boolean isGameEnded(int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public ChessGame.TeamColor getTeamTurn(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public void changeTeamTurn(int gameId) throws DataAccessException {

    }

    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, int gameID) throws DataAccessException {
        return List.of();
    }
}

