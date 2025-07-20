package bwillows.itemstackplaceholderapi.versioned.v1_19_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.server.v1_19_R1.ItemStack;
import net.minecraft.server.v1_19_R1.Packet;
import net.minecraft.server.v1_19_R1.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;


import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class SetSlotHandler {

    public static void handle(Packet<?> packet, Player player) {
        if (!(packet instanceof PacketPlayOutSetSlot)) return;

        try {
            ItemStack nmsItem = (ItemStack) getField(packet, "c");

            if (nmsItem == null || nmsItem.getTag() == null) return;

            org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
            ItemMeta meta = bukkitItem.getItemMeta();

            if (meta != null) {
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
                    bukkitItem.setItemMeta(meta);
                    ItemStack newNms = CraftItemStack.asNMSCopy(bukkitItem);
                    setField(packet, "c", newNms);
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutSetSlot");
            e.printStackTrace();
        }
    }

    private static Object getField(Object packet, String fieldName) throws Exception {
        Field field = packet.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(packet);
    }

    private static void setField(Object packet, String fieldName, Object value) throws Exception {
        Field field = packet.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(packet, value);
    }
}