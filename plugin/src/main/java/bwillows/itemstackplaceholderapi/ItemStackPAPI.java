package bwillows.itemstackplaceholderapi;

import bwillows.itemstackplaceholderapi.api.PacketHandlerInterface;
import bwillows.itemstackplaceholderapi.api.PlaceholderUtil;
import bwillows.itemstackplaceholderapi.api.ProtocolPacketHandlerInterface;
import bwillows.itemstackplaceholderapi.commands.ItemStackPAPICommand;
import bwillows.itemstackplaceholderapi.events.PlayerJoinListener;
import bwillows.itemstackplaceholderapi.events.PlayerQuitListener;
import bwillows.itemstackplaceholderapi.protocol.ProtocolLibInjector;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

public class ItemStackPAPI extends JavaPlugin {

    public static ItemStackPAPI instance;
    public static String version = "Unknown";
    public static Metrics metrics;
    public File pluginFolder;

    public static String NMS_VERSION;
    public static PacketHandlerInterface packetHandler;
    public static PlaceholderAPI placeholderAPI;

    public ItemStackPAPIConfig itemStackPAPIConfig;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupPlaceholderAPI()) {
            getLogger().severe("[ItemStackPlaceholderAPI] PlaceholderAPI not found or not enabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        NMS_VERSION = Utils.getNMSVersion();
        if (NMS_VERSION == null) {
            getLogger().severe("[ItemStackPlaceholderAPI] Unsupported NMS version");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(NMS_VERSION.equals("v1_17_R1") ||
           NMS_VERSION.equals("v1_18_R2") ||
           NMS_VERSION.equals("v1_19_R1") ||
           NMS_VERSION.equals("v1_19_R3") ||
           NMS_VERSION.equals("v1_20_R1") ||
           NMS_VERSION.equals("v1_20_R3")
        ) {
            // ProtocolLibInjector.init(this);
            getLogger().severe("[ItemStackPlaceholderAPI] Unsupported NMS version");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!loadPacketHandler()) {
            getLogger().severe("[ItemStackPlaceholderAPI] Failed to initialize NMS handler for version " + NMS_VERSION);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        itemStackPAPIConfig = new ItemStackPAPIConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        getCommand("itemstackplaceholderapi").setExecutor(new ItemStackPAPICommand());

        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                packetHandler.inject(player);
            }
        }

        try (InputStream in = getResource("project.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("version");

                String bStatsIDstring = props.getProperty("bstats-id");
                if(bStatsIDstring != null) {
                    int bStatsID = Integer.parseInt(bStatsIDstring);
                    metrics = new Metrics(this, bStatsID);
                    getLogger().info("[ItemStackPlaceholderAPI] Enabled bStats");
                } else {
                    getLogger().warning("[ItemStackPlaceholderAPI] bStats ID not found in project.properties.");
                }

            } else {
                getLogger().warning("[ItemStackPlaceholderAPI] project.properties not found in plugin jar.");
            }
        } catch (IOException e) {
            getLogger().warning("[ItemStackPlaceholderAPI] Failed to load version info:");
            e.printStackTrace();
        }
    }

    private boolean setupPlaceholderAPI() {
        PlaceholderUtil.placeholderAPIPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        return PlaceholderUtil.placeholderAPIPlugin != null && PlaceholderUtil.placeholderAPIPlugin.isEnabled();
    }

    private boolean loadPacketHandler() {
        try {
            if (NMS_VERSION.equals("v1_17_R1") ||
                NMS_VERSION.equals("v1_18_R2") ||
                NMS_VERSION.equals("v1_19_R1") ||
                NMS_VERSION.equals("v1_19_R3") ||
                NMS_VERSION.equals("v1_20_R1") ||
                NMS_VERSION.equals("v1_20_R3")
            ) {
                /*
                String handlerClassName = "bwillows.itemstackplaceholderapi.versioned." + NMS_VERSION + ".ProtocolPacketHandler";
                Class<?> clazz = Class.forName(handlerClassName);
                ProtocolPacketHandlerInterface instance = (ProtocolPacketHandlerInterface) clazz.newInstance();
                Method register = clazz.getMethod("register", Plugin.class);
                register.invoke(instance, this);

                getLogger().info("[ItemStackPlaceholderAPI] Loaded ProtocolLib packet handler for NMS version: " + NMS_VERSION);
                 */
            } else {
                String handlerClassName = "bwillows.itemstackplaceholderapi.versioned." + NMS_VERSION + ".PacketHandler";
                Class<?> clazz = Class.forName(handlerClassName);
                packetHandler = (PacketHandlerInterface) clazz.newInstance();

                getLogger().info("[ItemStackPlaceholderAPI] Loaded NMS handler: " + handlerClassName);
            }
            return true;
        } catch (Exception e) {
            getLogger().warning("[ItemStackPlaceholderAPI] Failed to load packet handler for NMS version: " + NMS_VERSION);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        PlaceholderUtil.placeholderAPIPlugin = null;
        if (packetHandler != null) {
            for (UUID uuid : packetHandler.getInjectedPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    packetHandler.uninject(player);
                }
            }
        }
    }
}