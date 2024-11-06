package com.jericho.cooldowns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;

public class DatabaseHandler {
    private final JerichoCooldowns plugin;
    private Connection connection;

    public DatabaseHandler(JerichoCooldowns plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/cooldowns.db");
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS cooldowns (uuid TEXT, action TEXT, expiry BIGINT)")) {
                statement.execute();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[JerichoCooldowns] Database connection failed: " + e.getMessage());
        }
    }

    public void saveCooldown(UUID playerId, String action, long expiry) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO cooldowns (uuid, action, expiry) VALUES (?, ?, ?)")) {
                statement.setString(1, playerId.toString());
                statement.setString(2, action);
                statement.setLong(3, expiry);
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[JerichoCooldowns] Failed to save cooldown: " + e.getMessage());
            }
        });
    }

    public Long getCooldown(UUID playerId, String action) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT expiry FROM cooldowns WHERE uuid = ? AND action = ?")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, action);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long expiry = resultSet.getLong("expiry");
                    if (System.currentTimeMillis() < expiry) {
                        return expiry;
                    } else {
                        removeCooldown(playerId, action);
                    }
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[JerichoCooldowns] Failed to retrieve cooldown: " + e.getMessage());
        }
        return null;
    }

    private void removeCooldown(UUID playerId, String action) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM cooldowns WHERE uuid = ? AND action = ?")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, action);
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[JerichoCooldowns] Failed to remove expired cooldown: " + e.getMessage());
        }
    }
}

