package bwillows.itemstackplaceholderapi.events;

import bwillows.itemstackplaceholderapi.ItemStackPAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void PlayerJoinListener(PlayerJoinEvent event) {
        ItemStackPAPI.packetHandler.inject(event.getPlayer());
    }
}
