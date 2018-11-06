package lsoleyl.mcmmo.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** This class holds the configuration info from the config file. This class has intentionally been named ConfigFile to
 *  prevent ambiguities with FML's Configuration class.
 */
public class ConfigFile {
    private static ConfigFile instance;

    public final boolean printSaveMessage;

    public final long progressBase;
    public final long progressSlope;

    private ConfigFile(Configuration config) {
        config.load();


        Property prop = config.get("general", "Print save messages", false);
        prop.comment = "May obscure other important log statements (default: false)";
        printSaveMessage = prop.getBoolean();

        prop = config.get("progression", "base", 1000);
        prop.comment = "XP required for next level = base + slope*level (default: 1000)";
        progressBase = prop.getInt();

        prop = config.get("progression", "slope", 20);
        prop.comment = "XP required for next level = base + slope*level (default: 20 - must be divisible by 2)";
        progressSlope = prop.getInt();

        config.save();
    }


    /** Returns the current config (may be null if not yet initialized)
     */
    public static ConfigFile getInstance() { return instance; }

    /** Should be called in preinit to initialize the configuration instance
     */
    public static void initialize(Configuration config) {
        instance = new ConfigFile(config);
    }
}
