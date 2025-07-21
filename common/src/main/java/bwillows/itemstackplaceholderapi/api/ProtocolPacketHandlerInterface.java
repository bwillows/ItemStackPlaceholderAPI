package bwillows.itemstackplaceholderapi.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface ProtocolPacketHandlerInterface {
    void register(Plugin plugin);
}
