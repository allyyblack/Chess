package dataaccess;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
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

    public boolean isGameValid(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT 1 FROM games WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if game exists: " + e.getMessage());
        }
    }

    public boolean isAuthTokenValid(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT 1 FROM tokens WHERE auth_token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if auth token exists: " + e.getMessage());
        }
    }



    public String getAuthToken(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT auth_token FROM tokens WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("auth_token");
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
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

    public GameData makeMove(ChessMove move, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM games WHERE gameID = ?";
            ChessGame game = null;
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var resu = ps.executeQuery()) {
                    if (resu.next()) {
                        String gameJson = resu.getString("game");
                        var serializer = new Gson();
                        game = serializer.fromJson(gameJson, ChessGame.class);
                    }
                }
            }
            if (game == null) {
                throw new DataAccessException("Game not found for gameID: " + gameID);
            }
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                throw new DataAccessException("Invalid move: " + e.getMessage());
            }
            var serializer = new Gson();
            String updatedGameJson = serializer.toJson(game);
            statement = "UPDATE games SET game = ? WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, updatedGameJson);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
            return getGame(gameID);
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game state: " + e.getMessage());
        }
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

    public void endGame(int gameID) throws DataAccessException {
        String statement = "UPDATE games SET gameStatus = 'FINISHED' WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("No game found with the provided gameID.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game status: " + e.getMessage());
        }
    }

    public Collection<ChessMove> getValidMoves(ChessPosition position, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT game FROM games WHERE gameID = ?";
            ChessGame chessGame = null;
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var res = ps.executeQuery()) {
                    if (res.next()) {
                        String gameJson = res.getString("game");
                        var serializer = new Gson();
                        chessGame = serializer.fromJson(gameJson, ChessGame.class);
                    }
                }
            }

            if (chessGame == null) {
                throw new DataAccessException("Game not found for gameID: " + gameID);
            }
            Collection<ChessMove> validMoves = chessGame.validMoves(position);
            return validMoves;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving valid moves for gameID: " + gameID);
        }
    }


    public boolean isGameEnded(int gameID) throws DataAccessException {
        String query = "SELECT gameStatus FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("gameStatus");
                        return "FINISHED".equalsIgnoreCase(status);
                    } else {
                        throw new DataAccessException("No game found with the provided gameID.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking game status: " + e.getMessage());
        }
    }



    public void removeUser(int gameID, String color) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String updateStatement;
            if (color.equalsIgnoreCase("WHITE")) {
                updateStatement = "UPDATE games SET whiteUsername = NULL WHERE gameID = ?";
            } else {
                updateStatement = "UPDATE games SET blackUsername = NULL WHERE gameID = ?";
            }

            try (var ps = conn.prepareStatement(updateStatement)) {
                ps.setInt(1, gameID);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("No game found with the provided gameID or the specified color was not set.");
                }
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error removing user from game: " + e.getMessage());
        }
    }


    public void updateGameState(int gameID, ChessGame chessGame) throws DataAccessException {
        // Serialize the ChessGame object to JSON
        String gameJson = new Gson().toJson(chessGame);

        // Define the SQL update statement
        String statement = "UPDATE games SET game = ? WHERE gameID = ?";

        // Execute the update
        executeUpdate(statement, gameJson, gameID);
    }


    public AuthData createAuth(String username) throws DataAccessException {
        String token =  UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        String statement = "INSERT INTO tokens (auth_token, username) VALUES (?, ?)";
        executeUpdate(statement, token, username);
        return authData;
    }

    public ChessGame.TeamColor getTeamTurn(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT teamTurn FROM games WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String teamTurnValue = rs.getString("teamTurn");
                        return ChessGame.TeamColor.valueOf(teamTurnValue.toUpperCase());
                    } else {
                        throw new DataAccessException("Game not found for gameID: " + gameId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game data for gameID: " + gameId);
        }
    }

    public void changeTeamTurn(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String selectStatement = "SELECT teamTurn FROM games WHERE gameID = ?";
            ChessGame.TeamColor currentTurn = null;
            try (var ps = conn.prepareStatement(selectStatement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String teamTurnValue = rs.getString("teamTurn");
                        currentTurn = ChessGame.TeamColor.valueOf(teamTurnValue.toUpperCase());
                    } else {
                        throw new DataAccessException("Game not found for gameID: " + gameId);
                    }
                }
            }
            ChessGame.TeamColor newTurn = (currentTurn == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            String updateStatement = "UPDATE games SET teamTurn = ? WHERE gameID = ?";
            try (var ps = conn.prepareStatement(updateStatement)) {
                ps.setString(1, newTurn.name());
                ps.setInt(2, gameId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("No game found with the provided gameID or update failed.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error changing team turn: " + e.getMessage());
        }
    }



    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT auth_token, username FROM tokens WHERE auth_token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    } else {
                        System.out.println("No matching token found for: " + authToken);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            throw new DataAccessException(String.format("Unable to read auth data: %s", e.getMessage()));
        }
        return null;
    }

    public boolean isInCheck(ChessGame game, ChessGame.TeamColor color) {
        return game.isInCheck(color);
    }

    public boolean isInCheckmate(ChessGame game, ChessGame.TeamColor color) {
        return game.isInCheckmate(color);
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
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
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
            `gameStatus` ENUM('ONGOING', 'CHECKMATE', 'STALEMATE', 'DRAW') DEFAULT 'ONGOING',
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


