package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;
import java.util.List;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }



    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE users");
        executeUpdate("TRUNCATE TABLE games");
        executeUpdate("TRUNCATE TABLE tokens");
    }

    public UserData createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
        return userData;
    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    public void updateGame(int gameID, String authToken, String color) throws DataAccessException {

    }

    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(AuthData authToken) throws DataAccessException {

    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
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
                `whiteUsername` VARCHAR(50) NOT NULL,
                `blackUsername` VARCHAR(50) NOT NULL,
                 `gameName` VARCHAR(100) NOT NULL,
                `game` TEXT NOT NULL, -- This can hold the serialized ChessGame data
                 PRIMARY KEY (`gameID`),
                FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`) ON DELETE CASCADE,
                 FOREIGN KEY (`blackUsername`) REFERENCES users(`username`) ON DELETE CASCADE
                 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
    };

    private void configureDatabase() throws DataAccessException {
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


