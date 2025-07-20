package bwillows.itemstackplaceholderapi;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private static final Map<String, String> MC_TO_NMS = new HashMap<>();

    static {
        MC_TO_NMS.put("1.8", "v1_8_R3");
        MC_TO_NMS.put("1.9", "v1_9_R2");
        MC_TO_NMS.put("1.10", "v1_10_R1");
        MC_TO_NMS.put("1.11", "v1_11_R1");
        MC_TO_NMS.put("1.12", "v1_12_R1");
        MC_TO_NMS.put("1.13", "v1_13_R2");
        MC_TO_NMS.put("1.14", "v1_14_R1");
        MC_TO_NMS.put("1.15", "v1_15_R1");
        MC_TO_NMS.put("1.16", "v1_16_R3");
        MC_TO_NMS.put("1.17", "v1_17_R1");
        MC_TO_NMS.put("1.18", "v1_18_R2");
        MC_TO_NMS.put("1.19", "v1_19_R3");
        MC_TO_NMS.put("1.20", "v1_20_R3");
    }

    /**
     * Attempts to parse the MC version from Bukkit.getVersion() and return the corresponding NMS version.
     * Returns null if not matched or an error occurs.
     */
    public static String getNMSVersion() {
        try {
            String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"
            int mcIndex = version.indexOf("(MC: ");
            if (mcIndex == -1) return null;

            String mcVersion = version.substring(mcIndex + 5, version.length() - 1).trim(); // "1.13.2"
            String[] parts = mcVersion.split("\\.");

            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            String key = "1." + minor;
            return (major == 1 && MC_TO_NMS.containsKey(key)) ? MC_TO_NMS.get(key) : null;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to resolve NMS version from Bukkit version.");
            return null;
        }
    }
}
