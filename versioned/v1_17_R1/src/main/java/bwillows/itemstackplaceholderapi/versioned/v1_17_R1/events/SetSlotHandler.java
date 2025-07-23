package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class SetSlotHandler {
    public static void handle(Object packet, Player viewer) {
        try {
            Method dMethod = packet.getClass().getDeclaredMethod("d");
            dMethod.setAccessible(true); // if method is private or protected
            net.minecraft.world.item.ItemStack itemStack = (net.minecraft.world.item.ItemStack)dMethod.invoke(packet);

            if (itemStack == null)
                return;

            itemStack = itemStack.cloneItemStack();

            Method getTag = itemStack.getClass().getDeclaredMethod("getTag");
            getTag.setAccessible(true);
            Object tag = getTag.invoke(itemStack);

            if(tag == null)
                return;

            org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(itemStack);
            ItemMeta meta = bukkitItem.getItemMeta();

            if (meta != null) {
                boolean modified = false;

                if (meta.hasDisplayName()) {
                    String newName = PlaceholderUtil.parsePlaceholders(viewer, meta.getDisplayName());
                    if (!newName.equals(meta.getDisplayName())) {
                        meta.setDisplayName(newName);
                        modified = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> newLore = meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(viewer, line))
                            .collect(Collectors.toList());

                    if (!newLore.equals(meta.getLore())) {
                        meta.setLore(newLore);
                        modified = true;
                    }
                }

                if (modified) {
                    bukkitItem.setItemMeta(meta);
                    net.minecraft.world.item.ItemStack newNms = CraftItemStack.asNMSCopy(bukkitItem);
                    setField(packet, "f", newNms);
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
