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

public class SetSlotHandler {

    public static void handle(PacketEvent event, Player player) {
        try {
            PacketContainer packet = event.getPacket();
            ItemStack item = packet.getItemModifier().read(0); // slot item

            if (item == null) return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            boolean modified = false;

            if (meta.hasDisplayName()) {
                String newName = PlaceholderUtil.parsePlaceholders(player, meta.getDisplayName());
                if (!newName.equals(meta.getDisplayName())) {
                    meta.setDisplayName(newName);
                    modified = true;
                }
            }

            if (meta.hasLore()) {
                List<String> newLore = meta.getLore().stream()
                        .map(line -> PlaceholderUtil.parsePlaceholders(player, line))
                        .collect(Collectors.toList());

                if (!newLore.equals(meta.getLore())) {
                    meta.setLore(newLore);
                    modified = true;
                }
            }

            if (modified) {
                item.setItemMeta(meta);
                packet.getItemModifier().write(0, item); // update the modified item back into the packet
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Failed to handle SET_SLOT packet");
            e.printStackTrace();
        }
    }
}