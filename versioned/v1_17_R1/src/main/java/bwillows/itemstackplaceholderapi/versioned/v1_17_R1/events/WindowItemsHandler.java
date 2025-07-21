package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class WindowItemsHandler {

    public static void handle(PacketEvent event, Player player) {
        try {
            PacketContainer packet = event.getPacket();

            List<ItemStack> items = packet.getItemListModifier().read(0);
            boolean modified = false;

            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item == null) continue;

                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                boolean itemChanged = false;

                if (meta.hasDisplayName()) {
                    String updated = PlaceholderUtil.parsePlaceholders(player, meta.getDisplayName());
                    if (!updated.equals(meta.getDisplayName())) {
                        meta.setDisplayName(updated);
                        itemChanged = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> updatedLore = meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(player, line))
                            .collect(Collectors.toList());
                    if (!updatedLore.equals(meta.getLore())) {
                        meta.setLore(updatedLore);
                        itemChanged = true;
                    }
                }

                if (itemChanged) {
                    item.setItemMeta(meta);
                    items.set(i, item);
                    modified = true;
                }
            }

            if (modified) {
                packet.getItemListModifier().write(0, items);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle WINDOW_ITEMS packet");
            e.printStackTrace();
        }
    }
}