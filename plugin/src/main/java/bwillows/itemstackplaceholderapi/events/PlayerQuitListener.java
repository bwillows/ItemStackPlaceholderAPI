package bwillows.itemstackplaceholderapi.events;

import bwillows.itemstackplaceholderapi.ItemStackPAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        ItemStackPAPI.packetHandler.uninject(event.getPlayer());
    }
}
