package com.jericho.cooldowns;

import org.bukkit.plugin.java.JavaPlugin;
import com.jericho.cooldowns.listeners.CooldownListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JerichoCooldowns extends JavaPlugin {

    private static JerichoCooldowns instance;
    private CooldownManager cooldownManager;
    private Connection connection;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupDatabase();
        cooldownManager = new CooldownManager(this);
        registerEvents();
    }

    private void setupDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/cooldowns.db");
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS cooldowns (playerUUID TEXT, action TEXT, expiry INTEGER, PRIMARY KEY (playerUUID, action));");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getDatabaseConnection() {
        return connection;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new CooldownListener(this), this);
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public static JerichoCooldowns getInstance() {
        return instance;
    }
}
