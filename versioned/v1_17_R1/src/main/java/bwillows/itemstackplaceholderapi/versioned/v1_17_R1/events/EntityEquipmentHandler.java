package bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events;

import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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
            boolean listChanged = false;

            Method cMethod = packet.getClass().getDeclaredMethod("c");
            cMethod.setAccessible(true); // if method is private or protected

            // List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>
            List<Object> list = (List<Object>) cMethod.invoke(packet);

            for (int i = 0; i < list.size(); i++) {
                Object pair = list.get(i);

                Method getFirst = pair.getClass().getMethod("getFirst");
                Method getSecond = pair.getClass().getMethod("getSecond");

                Object enumItemSlot = getFirst.invoke(pair);
                Object nmsItem = getSecond.invoke(pair);

                if (!(nmsItem instanceof ItemStack)) continue;
                ItemStack item = (ItemStack) nmsItem;

                item = item.cloneItemStack();

                Method getTag = item.getClass().getDeclaredMethod("getTag");
                getTag.setAccessible(true);
                Object tag = getTag.invoke(item);

                if(tag == null)
                    return;

                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(item);
                ItemMeta meta = bukkitItem.getItemMeta();
                if (meta == null) continue;

                boolean itemChanged = false;

                if (meta.hasDisplayName()) {
                    String updated = PlaceholderUtil.parsePlaceholders(viewer, meta.getDisplayName());
                    if (!updated.equals(meta.getDisplayName())) {
                        meta.setDisplayName(updated);
                        itemChanged = true;
                        listChanged = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> updatedLore = meta.getLore().stream()
                            .map(line -> PlaceholderUtil.parsePlaceholders(viewer, line))
                            .collect(Collectors.toList());
                    if (!updatedLore.equals(meta.getLore())) {
                        meta.setLore(updatedLore);
                        itemChanged = true;
                        listChanged = true;
                    }
                }

                if (itemChanged) {
                    bukkitItem.setItemMeta(meta);
                    ItemStack updatedNmsItem = CraftItemStack.asNMSCopy(bukkitItem);

                    Constructor<?> pairCtor = pair.getClass().getConstructor(Object.class, Object.class);
                    Object newPair = pairCtor.newInstance(enumItemSlot, updatedNmsItem);

                    list.set(i, newPair);
                }
            }
            if (listChanged) {
                setField(packet, "c", list);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Failed to handle PacketPlayOutEntityEquipment");
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
