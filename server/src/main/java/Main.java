import chess.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        DataAccess dataAccess = new MySqlDataAccess();
        int port = 8080;
        var service = new ChessService(dataAccess);
        var server = new Server(service).run(port);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

    }
}