package bwillows.itemstackplaceholderapi.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.PacketType;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class ProtocolLibInjector {

    public static void init(Plugin plugin) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS,
                PacketType.Play.Server.ENTITY_EQUIPMENT) {

            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                // handle your placeholder logic here
                // event.getPacket() gives access to PacketContainer
            }
        });
    }
}