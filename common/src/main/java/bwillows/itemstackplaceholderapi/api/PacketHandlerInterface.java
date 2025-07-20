package bwillows.itemstackplaceholderapi.api;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface PacketHandlerInterface {
    public Map<UUID, Object> handlers = new HashMap<>();

    void inject(Player player);
    void uninject(Player player);
    Collection<UUID> getInjectedPlayers();
}
