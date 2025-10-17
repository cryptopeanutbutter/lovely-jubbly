package dev.lixqa.amethystControl.ledger;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrystalLedger {
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS crystals (" +
            "uuid TEXT PRIMARY KEY, " +
            "minted_at INTEGER NOT NULL, " +
            "closed_at INTEGER" +
            ")";

    private final Logger logger;
    private final Connection connection;

    public CrystalLedger(JavaPlugin plugin) throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new SQLException("Unable to create plugin data folder");
        }

        this.logger = plugin.getLogger();

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }

        File database = new File(dataFolder, "ledger.db");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + database.getAbsolutePath());

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
        }
    }

    public synchronized String recordMint() {
        UUID uuid = UUID.randomUUID();
        long now = Instant.now().toEpochMilli();

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO crystals(uuid, minted_at) VALUES(?, ?)")) {
            statement.setString(1, uuid.toString());
            statement.setLong(2, now);
            statement.executeUpdate();
            return uuid.toString();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to record minted crystal", ex);
            return null;
        }
    }

    public synchronized boolean close(UUID uuid) {
        long now = Instant.now().toEpochMilli();

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE crystals SET closed_at = ? WHERE uuid = ? AND closed_at IS NULL")) {
            statement.setLong(1, now);
            statement.setString(2, uuid.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to close crystal " + uuid, ex);
            return false;
        }
    }

    public synchronized boolean exists(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM crystals WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to query crystal " + uuid, ex);
            return false;
        }
    }

    public synchronized int countMinted() {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM crystals")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to count minted crystals", ex);
            return 0;
        }
    }

    public synchronized int countOpen() {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM crystals WHERE closed_at IS NULL")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to count open crystals", ex);
            return 0;
        }
    }

    public synchronized void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to close ledger connection", ex);
        }
    }
}
