package bwillows.itemstackplaceholderapi.versioned.v1_19_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.server.v1_19_R1.ItemStack;
import net.minecraft.server.v1_19_R1.NonNullList;
import net.minecraft.server.v1_19_R1.Packet;
import net.minecraft.server.v1_19_R1.PacketPlayOutWindowItems;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;


public class WindowItemsHandler {

    public static void handle(Packet<?> packet, Player player) {
        if (!(packet instanceof PacketPlayOutWindowItems)) return;

        try {
            Object field = getField(packet, "b");

            if (!(field instanceof List)) return;

            @SuppressWarnings("unchecked")
            List<ItemStack> itemList = (List<ItemStack>) field;
            ItemStack[] items = itemList.toArray(new ItemStack[0]);
            boolean modified = false;

            for (int i = 0; i < items.length; i++) {
                ItemStack nmsItem = items[i];

                if (nmsItem == null || nmsItem.getTag() == null) continue;

                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
                ItemMeta meta = bukkitItem.getItemMeta();

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
                    bukkitItem.setItemMeta(meta);
                    items[i] = CraftItemStack.asNMSCopy(bukkitItem);
                    modified = true;
                }
            }

            if (modified) {
                NonNullList<ItemStack> newList = NonNullList.a();
                for (ItemStack item : items) {
                    newList.add(item);
                }
                setField(packet, "b", newList);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutWindowItems");
            e.printStackTrace();
        }
    }

    private static Object getField(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    private static void setField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}