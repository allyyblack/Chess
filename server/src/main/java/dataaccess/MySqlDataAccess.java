package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() {
        configureDatabase();
    }



    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM tokens");
        executeUpdate("DELETE FROM games");
        executeUpdate("DELETE FROM users");
    }

    public UserData createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        executeUpdate(statement, userData.username(), hashedPassword, userData.email());
        return userData;
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password_hash, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        var serializer = new Gson();
        var json = serializer.toJson(game);
        int gameID = executeUpdate(statement, gameName, null, null, json);
        return new GameData(gameID, null, null, gameName, game);
        }


        public GameData getGame(int gameID) throws DataAccessException {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readGame(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
            return null;
        }

    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> gamesList = new ArrayList<>();
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameJson = rs.getString("game");
                        var serializer = new Gson();
                        ChessGame chessGame = serializer.fromJson(gameJson, ChessGame.class);
                        gamesList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games: " + e.getMessage());
        }

        return gamesList;
    }

    public void updateGame(int gameID, String authToken, String color) throws DataAccessException {
//        GameData gameData = getGame(gameID);
        if (authToken == null) {
            throw new DataAccessException("authtoken null");
        }
        AuthData authData = getAuth(authToken);
        if (authData != null) {
            if (Objects.equals(color, "WHITE")) {
//                GameData updatedGame = new GameData(gameID, authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                String statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                executeUpdate(statement, authData.username(), gameID);
            }
            if (Objects.equals(color, "BLACK")) {
//                GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                String statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                executeUpdate(statement, authData.username(), gameID);
            }
        }



    }

    public AuthData createAuth(String username) throws DataAccessException {
        String token =  UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        String statement = "INSERT INTO tokens (auth_token, username) VALUES (?, ?)";
        executeUpdate(statement, token, username);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT auth_token, username FROM tokens WHERE auth_token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read auth data", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(AuthData authToken) throws DataAccessException {
        String statement = "DELETE FROM tokens WHERE auth_token = ?";
        executeUpdate(statement, authToken.authToken());
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var passwordHash = rs.getString("password_hash");
        var email = rs.getString("email");
        return new UserData(username, passwordHash, email);
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("game");
        var serializer = new Gson();
        ChessGame chessGame = serializer.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID,whiteUsername, blackUsername, gameName,chessGame);
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + statement);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` VARCHAR(50) NOT NULL,
              `password_hash` VARCHAR(256) NOT NULL,
             `email` VARCHAR(100) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS tokens (
               `auth_token` VARCHAR(256) NOT NULL,
               `username` VARCHAR(50) NOT NULL,
               PRIMARY KEY (`auth_token`),
               FOREIGN KEY (`username`) REFERENCES users(`username`) ON DELETE CASCADE
            )  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` INT NOT NULL AUTO_INCREMENT,
            `whiteUsername` VARCHAR(50) DEFAULT NULL,
            `blackUsername` VARCHAR(50) DEFAULT NULL,
            `gameName` VARCHAR(100) NOT NULL,
            `game` TEXT NOT NULL, -- This can hold the serialized ChessGame data
             PRIMARY KEY (`gameID`),
            FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`) ON DELETE SET NULL,
            FOREIGN KEY (`blackUsername`) REFERENCES users(`username`) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private void configureDatabase() {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Error configuring database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


