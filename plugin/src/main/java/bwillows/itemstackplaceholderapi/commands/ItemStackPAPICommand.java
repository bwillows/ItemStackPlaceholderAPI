package bwillows.itemstackplaceholderapi.commands;

import bwillows.itemstackplaceholderapi.ItemStackPAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ItemStackPAPICommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemstackplaceholderapi.*")) {
            String message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getString("no-permission");
            if (!(message == null) && !message.trim().isEmpty()) {
                message = message.replace("§", "&");
                message = ChatColor.translateAlternateColorCodes('&', message);
                message = message.replace("%version%", ItemStackPAPI.version);
                sender.sendMessage(message);
            }
            return true;
        }

        if (args.length == 0) {
            List<String> message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getStringList("no-argument");
            if(!(message == null)) {
                for (String line : message) {
                    line = line.replace("§", "&");
                    line = ChatColor.translateAlternateColorCodes('&', line);
                    line = line.replace("%version%", ItemStackPAPI.version);
                    sender.sendMessage(line);
                }
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
            {
                List<String> message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getStringList("help");
                if(!(message == null)) {
                    for (String line : message) {
                        line = line.replace("§", "&");
                        line = ChatColor.translateAlternateColorCodes('&', line);
                        line = line.replace("%version%", ItemStackPAPI.version);
                        sender.sendMessage(line);
                    }
                }
            }
            return true;
            case "ver":
            case "version":
            {
                String message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getString("version");
                if(!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", ItemStackPAPI.version);
                    sender.sendMessage(message);
                }
            }
            return true;
            case "reload":
            {
                ItemStackPAPI.instance.itemStackPAPIConfig.reload();
                String message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getString("reload");
                if (!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", ItemStackPAPI.version);
                    sender.sendMessage(message);
                }
            }
            return true;
            default:
            {
                String message = ItemStackPAPI.instance.itemStackPAPIConfig.langYml.getString("invalid-argument");
                if (!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", ItemStackPAPI.version);
                    sender.sendMessage(message);
                }
            }
            return  true;
        }
    }
}

