package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB {

    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveMessage(String sender, String receiver, String content) {
        String sql =
                "INSERT INTO messages " +
                "(sender, receiver, content) " +
                "VALUES (?, ?, ?)";

        try (
            Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, content);
            stmt.executeUpdate();
        } 
        catch (SQLException e) {
            ServerMain.log("Database Error: " + e.getMessage());
        }
    }
}