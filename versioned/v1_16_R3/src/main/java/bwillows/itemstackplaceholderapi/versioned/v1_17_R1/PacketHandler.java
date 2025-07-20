package bwillows.itemstackplaceholderapi.versioned.v1_17_R1;

import bwillows.itemstackplaceholderapi.api.PacketHandlerInterface;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class PacketHandler implements PacketHandlerInterface {
    @Override
    public void inject(Player player) {
        UUID uuid = player.getUniqueId();
        if(!handlers.containsKey(uuid)) {
            NettyPacketHandler nettyPacketHandler = new NettyPacketHandler(player);
            nettyPacketHandler.inject();
            handlers.put(uuid, nettyPacketHandler);
        }
    }

    @Override
    public void uninject(Player player) {
        UUID uuid = player.getUniqueId();
        Object handler = handlers.get(uuid);
        if(handler != null) {
            NettyPacketHandler nettyPacketHandler = (NettyPacketHandler) handler;
            nettyPacketHandler.uninject();
        }
        handlers.remove(uuid);
    }

    @Override
    public Collection<UUID> getInjectedPlayers() {
        return handlers.keySet();
    }
}