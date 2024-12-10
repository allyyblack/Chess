package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.PlayerGame;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.Objects;

public class ChessService {
    private final DataAccess dataAccess;
    private int gameId;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Chess is not very simple

    public AuthData login(String username, String password) throws DataAccessException{
        UserData userData = new UserData(username, password, null);
        UserData foundUser = dataAccess.getUser(userData.username());
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (foundUser == null || !BCrypt.checkpw(password, foundUser.password())) {
            throw new DataAccessException("");
        }
        return dataAccess.createAuth(username);
    }

    public UserData register(String username, String password, String email) throws DataAccessException {
        UserData userInfo = new UserData(username, password, email);
        UserData foundUser = dataAccess.getUser(userInfo.username());
        if (foundUser != null) {
            throw new DataAccessException("");
        }
        return dataAccess.createUser(userInfo);
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        return dataAccess.createGame(gameName);
    }

    public GameData makeMove(ChessMove move, int gameID, String authToken)
            throws DataAccessException, UnauthorizedAccessException, InvalidMoveException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found for gameID: " + gameID);
        }

        ChessGame chessGame = gameData.game();
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new InvalidMoveException("Invalid move: " + e.getMessage());
        }
        dataAccess.updateGameState(gameID, chessGame);
        return dataAccess.getGame(gameID);
    }

    public boolean isGameValid(int gameId) throws DataAccessException {
        this.gameId = gameId;
        return dataAccess.isGameValid(gameId);
    }

    public boolean isAuthTokenValid(String authToken) throws DataAccessException {
        return dataAccess.isAuthTokenValid(authToken);
    }

    public ChessGame.TeamColor getTeamTurn(int gameId) throws DataAccessException {
        return dataAccess.getTeamTurn(gameId);
    }

    public void changeTeamTurn(int gameId) throws DataAccessException {
        dataAccess.changeTeamTurn(gameId);
    }



    public String getUserColor(int gameId, String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            return "invalid auth";
        }
        GameData gameData = dataAccess.getGame(gameId);
        if (authData.username().equals(gameData.whiteUsername())) {
            return "WHITE";
        } else if (authData.username().equals(gameData.blackUsername())) {
            return "BLACK";
        }
        return "not in the game";
    }

    public boolean isInCheck(ChessGame game, ChessGame.TeamColor color) {
        return dataAccess.isInCheck(game, color);
    }

    public boolean isMoveValid(int gameId, ChessMove move) throws DataAccessException, InvalidMoveException {
        ChessGame game = dataAccess.getGame(gameId).game();
        return game.isMoveValid(move);
    }


    public boolean isInCheckmate(ChessGame game, ChessGame.TeamColor color) {
        return dataAccess.isInCheckmate(game, color);
    }

    public void joinGame(PlayerGame playerGame, String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        if (Objects.equals(playerGame.playerColor(), "WHITE")) {
            if (dataAccess.getGame(playerGame.gameID()).whiteUsername() != null) {
                throw new DataAccessException("White player slot is already filled");
            } else {
                dataAccess.updateGame(playerGame.gameID(), authToken, playerGame.playerColor());
            }
        }
        if (Objects.equals(playerGame.playerColor(), "BLACK")) {
            if (dataAccess.getGame(playerGame.gameID()).blackUsername() != null) {
                throw new DataAccessException("Black player slot is already filled");
            } else {
                dataAccess.updateGame(playerGame.gameID(), authToken, playerGame.playerColor());
            }
        }
    }

    public void leave(PlayerGame playerGame, String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        GameData gameData = dataAccess.getGame(playerGame.gameID());
        if (gameData == null) {
            throw new DataAccessException("Game not found for gameID: " + playerGame.gameID());
        }

        if (authData.username().equals(gameData.whiteUsername())) {
            gameData = gameData.withWhiteUsername(null);
            dataAccess.removeUser(gameData.gameID(), playerGame.playerColor());
        }
        else if (authToken.equals(gameData.blackUsername())) {
            // Create a new GameData instance with black player removed
            gameData = gameData.withBlackUsername(null);
            dataAccess.removeUser(gameData.gameID(), playerGame.playerColor());
        } else {
            throw new DataAccessException(authData.username() + " is not part of this game.");
        }
    }

    public void clearApplication() throws DataAccessException {
        dataAccess.clear();
    }

    public GameData getgame(int gameId) throws DataAccessException {
        GameData game = dataAccess.getGame(gameId);
        return game;
    }

    public String getUser(String authToken) throws DataAccessException {
            AuthData authData = dataAccess.getAuth(authToken);
            if(authData == null) {
                return "invalid auth";
            }
            return authData.username();
    }

    public String getOtherUser(String authToken, int gameId) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            return "invalid auth";
        }
        String blackUser = dataAccess.getGame(gameId).blackUsername();
        String whiteUser = dataAccess.getGame(gameId).whiteUsername();
        AuthData otherAuthData = dataAccess.getAuth(authToken);
        if(otherAuthData.username().equals(whiteUser)) {
            return dataAccess.getAuthToken(blackUser);
        }
        else{
            return dataAccess.getAuthToken(whiteUser);
        }
    }


    public Collection<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        return dataAccess.listGames();

    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        dataAccess.deleteAuth(authData);
    }

    public void endGame(int gameId) throws DataAccessException {
        dataAccess.endGame(gameId);
    }

    public boolean isGameEnded(int gameId) throws DataAccessException {
        return dataAccess.isGameEnded(gameId);
    }

    public Collection<ChessMove> validMoves(ChessPosition position, int gameId) throws DataAccessException {
        return dataAccess.getValidMoves(position, gameId);
    }
}