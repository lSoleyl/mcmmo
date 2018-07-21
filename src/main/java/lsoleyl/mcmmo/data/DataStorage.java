package lsoleyl.mcmmo.data;


import com.google.gson.Gson;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataStorage {
    // As the current directory is always the server directory, we don't have to retrieve this path ourselves
    public static final String FILE_PATH = ".\\saves\\mcmmo\\xp.json";

    // We are assuming that the player name is unique... this should work
    private Map<String/*displayName*/, PlayerXp> playerMap = new HashMap<>();

    private DataStorage() { loadData(); }

    /** Creates a data storage object and initialized it by loading the saved data and
     *  registering for the worldSaved event
     *
     * @return the created data storage object
     */
    public static DataStorage Initialize() {
        DataStorage storage = new DataStorage();
        MinecraftForge.EVENT_BUS.register(storage);
        return storage;
    }

    private static File getSaveFile() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // create missing directories if necessary
        return file;
    }

    /** Retrieves the xp state for the given player entity. If the player is not yet known to
     *  the datastorage, a new playerXp object is created for that player.
     *
     * @param player the player to retrieve the data for
     * @return the player's xp data, which may be manipulated
     */
    public PlayerXp get(EntityPlayer player) {
        String playerName = player.getDisplayName();
        if (!playerMap.containsKey(playerName)) {
            playerMap.put(playerName, new PlayerXp());
        }

        return playerMap.get(playerName);
    }

    private void loadData() {
        System.out.println("Loading MCMMO data");
        Gson gson = new Gson();

        // load the save file if it already exists
        if (getSaveFile().exists()) {
            try (FileReader reader = new FileReader(getSaveFile())) {
                playerMap = gson.fromJson(reader, playerMap.getClass());
                if (playerMap == null) {
                    playerMap = new HashMap<>();
                }
            } catch (IOException ex) {
                System.err.println("Failed to load MCMMO data. Error: " + ex);
            }
        }
    }


    @SubscribeEvent
    public void onWorldSaved(WorldEvent.Save event) {
        //TODO how do I only save once and not thrice? The dimension-check doesn't work.

        // Save the player data using GSON
        System.out.println("Saving MCMMO data");
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(getSaveFile())) {
            writer.write(gson.toJson(playerMap));
        } catch (IOException ex) {
            System.err.println("Failed to save MCMMO data. Error: " + ex);
        }
    }


}
