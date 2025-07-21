package bwillows.itemstackplaceholderapi.versioned.v1_17_R1;

import bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ProtocolPacketHandler {

    public static void register(Plugin plugin) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS,
                PacketType.Play.Server.ENTITY_EQUIPMENT) {

            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                PacketType type = event.getPacketType();

                if (type == PacketType.Play.Server.SET_SLOT) {
                    SetSlotHandler.handle(event, player);
                } else if (type == PacketType.Play.Server.WINDOW_ITEMS) {
                    WindowItemsHandler.handle(event, player);
                } else if (type == PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    EntityEquipmentHandler.handle(event, player);
                }
            }
        });
    }
}