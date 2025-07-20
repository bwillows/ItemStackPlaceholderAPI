package bwillows.itemstackplaceholderapi.api;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderUtil {
    public static Plugin placeholderAPIPlugin;
    public static PlaceholderAPI placeholderAPI;
    public static String parsePlaceholders(Player player, String input) {
        if (player == null || input == null || placeholderAPIPlugin == null) return input;
        try {
            return PlaceholderAPI.setPlaceholders(player, input);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to parse placeholders for player " + player.getName());
            return input;
        }
    }
}
