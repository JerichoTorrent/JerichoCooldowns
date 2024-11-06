package com.jericho.cooldowns;

import com.jericho.cooldowns.JerichoCooldowns;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap<>();
    private final JerichoCooldowns plugin;

    public CooldownManager(JerichoCooldowns plugin) {
        this.plugin = plugin;
        loadCooldownsFromDatabase();
    }

    public boolean isOnCooldown(Player player, String action) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(action) || !cooldowns.get(action).containsKey(playerId)) {
            return false;
        }
        long expiry = cooldowns.get(action).get(playerId);
        return System.currentTimeMillis() <= expiry;
    }

    public void setCooldown(Player player, String action, long duration) {
        UUID playerId = player.getUniqueId();
        cooldowns.computeIfAbsent(action, k -> new HashMap<>()).put(playerId, System.currentTimeMillis() + duration);
        saveCooldownToDatabase(player, action, System.currentTimeMillis() + duration);
        sendCooldownMessage(player, action, duration / 1000);
    }

    public long getTimeLeft(Player player, String action) {
        if (isOnCooldown(player, action)) {
            return (cooldowns.get(action).get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    private void loadCooldownsFromDatabase() {
        Connection conn = plugin.getDatabaseConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("SELECT playerUUID, action, expiry FROM cooldowns")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("playerUUID"));
                String action = rs.getString("action");
                long expiry = rs.getLong("expiry");
                cooldowns.computeIfAbsent(action, k -> new HashMap<>()).put(playerUUID, expiry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveCooldownToDatabase(Player player, String action, long expiry) {
        Connection conn = plugin.getDatabaseConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("REPLACE INTO cooldowns (playerUUID, action, expiry) VALUES (?, ?, ?);")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, action);
            stmt.setLong(3, expiry);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendCooldownMessage(Player player, String action, long timeLeft) {
    }

    public void checkAndUpdateCooldowns() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (String action : cooldowns.keySet()) {
                HashMap<UUID, Long> actionCooldowns = cooldowns.get(action);
                for (UUID playerId : actionCooldowns.keySet()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && isOnCooldown(player, action)) {
                        long timeLeft = getTimeLeft(player, action);
                        sendCooldownMessage(player, action, timeLeft);
                    }
                }
            }
        }, 0L, 20L);
    }
}
