package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class WindowItemsHandler {
    public static void handle(Object packet, Player viewer) {
        try {

            Method cMethod = packet.getClass().getDeclaredMethod("c");
            cMethod.setAccessible(true); // if method is private or protected
            List<ItemStack> itemList = (List<ItemStack>)cMethod.invoke(packet);

            boolean modified = false;

            for(ItemStack nmsItem : itemList) {
                if (nmsItem == null) continue;

                Method getTag = nmsItem.getClass().getDeclaredMethod("getTag");
                getTag.setAccessible(true);
                Object tag = getTag.invoke(nmsItem);

                if(tag == null) continue;

                nmsItem = nmsItem.cloneItemStack();
                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
                ItemMeta meta = bukkitItem.getItemMeta();

                if (meta == null) continue;

                boolean itemChanged = false;

                if (meta.hasDisplayName()) {
                    String updated = PlaceholderUtil.parsePlaceholders(viewer, meta.getDisplayName());
                    if (!updated.equals(meta.getDisplayName())) {
                        meta.setDisplayName(updated);
                        itemChanged = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> updatedLore = meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(viewer, line))
                            .collect(Collectors.toList());

                    if (!updatedLore.equals(meta.getLore())) {
                        meta.setLore(updatedLore);
                        itemChanged = true;
                    }
                }

                if (itemChanged) {
                    bukkitItem.setItemMeta(meta);
                    nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
                    modified = true;
                }
            }


            if (modified) {
                setField(packet, "c", itemList);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutWindowItems");
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
