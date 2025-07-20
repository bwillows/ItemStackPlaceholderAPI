package bwillows.itemstackplaceholderapi.versioned.v1_10_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

public class EntityEquipmentHandler {

    public static void handle(Packet<?> packet, Player viewer) {
        if (!(packet instanceof PacketPlayOutEntityEquipment)) return;

        try {
            ItemStack nmsItem = (ItemStack) getField(packet, "c");

            if (nmsItem == null || nmsItem.getTag() == null) return;

            org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
            ItemMeta meta = bukkitItem.getItemMeta();

            if (meta != null) {
                if (meta.hasDisplayName()) {
                    meta.setDisplayName(PlaceholderUtil.parsePlaceholders(viewer, meta.getDisplayName()));
                }

                if (meta.hasLore()) {
                    meta.setLore(meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(viewer, line))
                            .collect(java.util.stream.Collectors.toList()));
                }

                bukkitItem.setItemMeta(meta);

                // Convert back to NMS and re-assign
                ItemStack newNms = CraftItemStack.asNMSCopy(bukkitItem);
                setField(packet, "c", newNms);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutEntityEquipment");
            e.printStackTrace();
        }
    }

    private static Object getField(Object packet, String name) throws Exception {
        Field field = packet.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(packet);
    }

    private static void setField(Object packet, String name, Object value) throws Exception {
        Field field = packet.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(packet, value);
    }
}