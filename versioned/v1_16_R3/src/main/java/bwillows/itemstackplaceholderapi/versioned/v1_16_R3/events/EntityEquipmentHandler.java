package bwillows.itemstackplaceholderapi.versioned.v1_16_R3.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class EntityEquipmentHandler {

    public static void handle(Object packet, Player viewer) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) getField(packet, "b");
            if (list == null || list.isEmpty()) return;

            for (int i = 0; i < list.size(); i++) {
                Object pair = list.get(i);

                Method getFirst = pair.getClass().getMethod("getFirst");
                Method getSecond = pair.getClass().getMethod("getSecond");

                Object enumItemSlot = getFirst.invoke(pair);
                Object nmsItem = getSecond.invoke(pair);

                if (!(nmsItem instanceof ItemStack)) continue;
                ItemStack item = (ItemStack) nmsItem;
                if (item == null || item.getTag() == null) continue;

                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(item);
                ItemMeta meta = bukkitItem.getItemMeta();
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
                    bukkitItem.setItemMeta(meta);
                    ItemStack updatedNmsItem = CraftItemStack.asNMSCopy(bukkitItem);

                    Constructor<?> pairCtor = pair.getClass().getConstructor(Object.class, Object.class);
                    Object newPair = pairCtor.newInstance(enumItemSlot, updatedNmsItem);

                    list.set(i, newPair);
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutEntityEquipment");
            e.printStackTrace();
        }
    }

    private static Object getField(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }
}