package me.phuongaz.thesieure.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;
import java.util.List;

public class SQLiteProvider extends Provider{

    private static final String URL = "jdbc:sqlite:thesieure.db";
    private static final String CARDS_TABLE = "cards";
    private static final String PENDING_TABLE = "pending";

    private Connection connection;

    public SQLiteProvider() {}

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createCardsTable = "CREATE TABLE IF NOT EXISTS " + CARDS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, serial TEXT, pin TEXT, amount INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String createPendingTable = "CREATE TABLE IF NOT EXISTS " + PENDING_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, serial TEXT, pin TEXT, amount INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createCardsTable);
            statement.executeUpdate(createPendingTable);
        }
    }

    @Override
    public void insertCard(String player, String serial, String pin, int amount) {
        String insertQuery = "INSERT INTO " + CARDS_TABLE + " (player, serial, pin, amount) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, serial);
            preparedStatement.setString(3, pin);
            preparedStatement.setInt(4, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertPending(String player, String serial, String pin, int amount) {
        String insertQuery = "INSERT INTO " + PENDING_TABLE + " (player, serial, pin, amount) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, serial);
            preparedStatement.setString(3, pin);
            preparedStatement.setInt(4, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertBank(String player, int amount) {
        // todo
    }

    @Override
    public List<String> getPending(String player) {
        String selectQuery = "SELECT serial, pin FROM " + PENDING_TABLE + " WHERE player = ?";
        List<String> pending = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                pending.add(resultSet.getString("serial") + ":" + resultSet.getString("pin"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pending;
    }

    @Override
    public void removePending(String serial, String pin) {
        String deleteQuery = "DELETE FROM " + PENDING_TABLE + " WHERE serial = ? AND pin = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, serial);
            preparedStatement.setString(2, pin);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
