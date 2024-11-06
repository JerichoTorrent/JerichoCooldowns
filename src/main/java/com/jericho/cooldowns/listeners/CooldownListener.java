package com.jericho.cooldowns.listeners;

import com.jericho.cooldowns.CooldownManager;
import com.jericho.cooldowns.JerichoCooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;

public class CooldownListener implements Listener {
    private final CooldownManager cooldownManager;

    public CooldownListener(JerichoCooldowns plugin) {
        this.cooldownManager = plugin.getCooldownManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

@EventHandler
public void onThrowEnderPearl(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item.getType() == Material.ENDER_PEARL && 
        (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
        if (cooldownManager.isOnCooldown(player, "pearl")) {
            event.setCancelled(true);
        } else {
            Bukkit.getScheduler().runTaskLater(JerichoCooldowns.getInstance(), () -> {
                cooldownManager.setCooldown(player, "pearl", 5000);
                player.setCooldown(Material.ENDER_PEARL, 5 * 20);
            }, 1L);
        }
    }
}

    @EventHandler
    public void onEatGoldenApple(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material type = event.getItem().getType();
        String action = type == Material.GOLDEN_APPLE ? "apple" : "enchantedApple";
        if (type == Material.GOLDEN_APPLE || type == Material.ENCHANTED_GOLDEN_APPLE) {
            if (!cooldownManager.isOnCooldown(player, action)) {
                cooldownManager.setCooldown(player, action, 10000);
                player.setCooldown(type, 10 * 20);
            }
        }
    }
}

