package bwillows.itemstackplaceholderapi;


import bwillows.itemstackplaceholderapi.api.PacketHandlerInterface;
import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import bwillows.itemstackplaceholderapi.commands.ItemStackPAPICommand;
import bwillows.itemstackplaceholderapi.events.PlayerJoinListener;
import bwillows.itemstackplaceholderapi.events.PlayerQuitListener;
import bwillows.itemstackplaceholderapi.versioned.v1_8_R3.PacketHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class ItemStackPAPI extends JavaPlugin {
    public static ItemStackPAPI instance;
    public static String version = "Unknown";
    public File pluginFolder;

    public static String NMS_VERSION;
    public static PacketHandlerInterface packetHandler;
    public static PlaceholderAPI placeholderAPI;

    public ItemStackPAPIConfig itemStackPAPIConfig;

    @Override
    public void onEnable() {
        bwillows.itemstackplaceholderapi.ItemStackPAPI.instance = this;

        Properties props = new Properties();
        try (InputStream in = getResource("version.properties")) {
            if (in != null) {
                props.load(in);
                bwillows.itemstackplaceholderapi.ItemStackPAPI.version = props.getProperty("version");
            } else {
                Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] version.properties not found in plugin jar.");
            }
        } catch (IOException exception) {
            Bukkit.getLogger().warning("[ItemStackPlaceholderAPI] Unhandled exception loading version info");
            exception.printStackTrace();
        }

        PlaceholderUtil.placeholderAPIPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if(PlaceholderUtil.placeholderAPIPlugin == null || !Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
            Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Failed to load PlaceholderAPI");
            Bukkit.getPluginManager().disablePlugin(bwillows.itemstackplaceholderapi.ItemStackPAPI.instance);
            return;
        }

        ItemStackPAPI.NMS_VERSION = Utils.getNMSVersion();

        if(ItemStackPAPI.NMS_VERSION == null) {
            Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Unsupported NMS version");
            Bukkit.getPluginManager().disablePlugin(bwillows.itemstackplaceholderapi.ItemStackPAPI.instance);
            return;
        } else {
            try {
                String handlerClassName = "bwillows.itemstackplaceholderapi.versioned." + NMS_VERSION + ".PacketHandler";
                Class<?> clazz = Class.forName("bwillows.itemstackplaceholderapi.versioned." + NMS_VERSION + ".PacketHandler");
                packetHandler = (PacketHandlerInterface) clazz.newInstance();

                Bukkit.getLogger().info("[ItemStackPlaceholderAPI] Loaded NMS handler: " + handlerClassName);
            } catch (Exception e) {
                Bukkit.getLogger().severe("[ItemStackPlaceholderAPI] Failed to load PacketHandler for " + NMS_VERSION);
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        itemStackPAPIConfig = new ItemStackPAPIConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);


        getCommand("itemstackplaceholderapi").setExecutor(new ItemStackPAPICommand());

        // TODO: fix to be Bukkit.getonlinePlayers()
        // TODO: ... currently a duplicate method when importing everything the way I am?

        for(World world : Bukkit.getWorlds()) {
            for(Player player : world.getPlayers()) {
                packetHandler.inject(player);
            }
        }
    }

    @Override
    public void onDisable() {
        PlaceholderUtil.placeholderAPIPlugin = null;
        for(UUID uuid : PacketHandler.handlers.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null)
                continue;
            packetHandler.uninject(player);
        }
    }
}

