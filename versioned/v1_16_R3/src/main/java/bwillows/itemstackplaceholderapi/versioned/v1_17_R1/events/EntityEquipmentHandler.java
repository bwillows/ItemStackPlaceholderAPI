package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntityEquipmentHandler {

    public static void handle(Packet<?> packet, Player viewer) {
        if (!(packet instanceof PacketPlayOutEntityEquipment)) return;

        try {
            ItemStack nmsItem = (ItemStack) getField(packet, "c");

            if (nmsItem == null || nmsItem.getTag() == null) return;

            org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
            ItemMeta meta = bukkitItem.getItemMeta();

            if (meta == null) return;

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
                bukkitItem.setItemMeta(meta);
                ItemStack updatedNmsItem = CraftItemStack.asNMSCopy(bukkitItem);
                setField(packet, "c", updatedNmsItem);
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