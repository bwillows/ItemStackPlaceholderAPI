package bwillows.itemstackplaceholderapi;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ItemStackPAPIConfig {
    public ItemStackPAPIConfig() {
        reload();
    }

    public File langFile;
    public FileConfiguration langYml;

    public void reload() {
        langFile = new File(ItemStackPAPI.instance.pluginFolder, "lang.yml");
        if (!langFile.exists()) {
            ItemStackPAPI.instance.saveResource("lang.yml", false);
        }
        langYml = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(langFile);
    }
}
