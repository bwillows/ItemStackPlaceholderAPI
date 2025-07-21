package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class EntityEquipmentHandler {

    public static void handle(PacketEvent event, Player viewer) {
        try {
            List<Pair<ItemSlot, ItemStack>> list = event.getPacket().getSlotStackPairLists().read(0);

            for (int i = 0; i < list.size(); i++) {
                Pair<ItemSlot, ItemStack> pair = list.get(i);
                ItemStack item = pair.getSecond();

                if (item == null) continue;

                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                boolean changed = false;

                if (meta.hasDisplayName()) {
                    String updated = PlaceholderUtil.parsePlaceholders(viewer, meta.getDisplayName());
                    if (!updated.equals(meta.getDisplayName())) {
                        meta.setDisplayName(updated);
                        changed = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> updatedLore = meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(viewer, line))
                            .collect(Collectors.toList());
                    if (!updatedLore.equals(meta.getLore())) {
                        meta.setLore(updatedLore);
                        changed = true;
                    }
                }

                if (changed) {
                    item.setItemMeta(meta);
                    Pair<ItemSlot, ItemStack> newPair = new Pair<>(pair.getFirst(), item);
                    list.set(i, newPair);
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle ENTITY_EQUIPMENT packet");
            e.printStackTrace();
        }
    }
}