package bwillows.itemstackplaceholderapi;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private static final Map<String, String> MC_TO_NMS = new HashMap<>();

    static {
        // 1.8
        MC_TO_NMS.put("1.8.8", "v1_8_R3");

        // 1.9
        MC_TO_NMS.put("1.9.0", "v1_9_R1");
        MC_TO_NMS.put("1.9.1", "v1_9_R1");
        MC_TO_NMS.put("1.9.2", "v1_9_R1");
        MC_TO_NMS.put("1.9.3", "v1_9_R2");
        MC_TO_NMS.put("1.9.4", "v1_9_R2");

        // 1.10
        MC_TO_NMS.put("1.10.0", "v1_10_R1");
        MC_TO_NMS.put("1.10.2", "v1_10_R1");

        // 1.11
        MC_TO_NMS.put("1.11.0", "v1_11_R1");
        MC_TO_NMS.put("1.11.2", "v1_11_R1");

        // 1.12
        MC_TO_NMS.put("1.12.0", "v1_12_R1");
        MC_TO_NMS.put("1.12.1", "v1_12_R1");
        MC_TO_NMS.put("1.12.2", "v1_12_R1");

        // 1.13
        MC_TO_NMS.put("1.13.0", "v1_13_R2");
        MC_TO_NMS.put("1.13.1", "v1_13_R2");
        MC_TO_NMS.put("1.13.2", "v1_13_R2");

        // 1.14
        MC_TO_NMS.put("1.14.0", "v1_14_R1");
        MC_TO_NMS.put("1.14.1", "v1_14_R1");
        MC_TO_NMS.put("1.14.2", "v1_14_R1");
        MC_TO_NMS.put("1.14.3", "v1_14_R1");
        MC_TO_NMS.put("1.14.4", "v1_14_R1");

        // 1.15
        MC_TO_NMS.put("1.15.0", "v1_15_R1");
        MC_TO_NMS.put("1.15.1", "v1_15_R1");
        MC_TO_NMS.put("1.15.2", "v1_15_R1");

        // 1.16
        MC_TO_NMS.put("1.16.0", "v1_16_R3");
        MC_TO_NMS.put("1.16.1", "v1_16_R3");
        MC_TO_NMS.put("1.16.2", "v1_16_R3");
        MC_TO_NMS.put("1.16.3", "v1_16_R3");
        MC_TO_NMS.put("1.16.4", "v1_16_R3");
        MC_TO_NMS.put("1.16.5", "v1_16_R3");

        // 1.17
        MC_TO_NMS.put("1.17.0", "v1_17_R1");
        MC_TO_NMS.put("1.17.1", "v1_17_R1");

        // 1.18
        MC_TO_NMS.put("1.18.0", "v1_18_R2");
        MC_TO_NMS.put("1.18.1", "v1_18_R2");
        MC_TO_NMS.put("1.18.2", "v1_18_R2");

        // 1.19
        MC_TO_NMS.put("1.19.0", "v1_19_R1");
        MC_TO_NMS.put("1.19.1", "v1_19_R1");
        MC_TO_NMS.put("1.19.2", "v1_19_R1");

        MC_TO_NMS.put("1.19.3", "v1_19_R3");
        MC_TO_NMS.put("1.19.4", "v1_19_R3");

        // 1.20
        MC_TO_NMS.put("1.20.0", "v1_20_R1");
        MC_TO_NMS.put("1.20.1", "v1_20_R1");

        MC_TO_NMS.put("1.20.2", "v1_20_R3");
        MC_TO_NMS.put("1.20.3", "v1_20_R3");
        MC_TO_NMS.put("1.20.4", "v1_20_R3");
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

            if(!MC_TO_NMS.containsKey(mcVersion)) {
                return null;
            } else {
                return MC_TO_NMS.get(mcVersion);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to resolve NMS version from Bukkit version.");
            return null;
        }
    }
}
