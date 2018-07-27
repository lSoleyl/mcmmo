package lsoleyl.mcmmo.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.utility.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataStorage {
    // As the current directory is always the server directory, we don't have to retrieve this path ourselves
    public static final String FILE_PATH = "." + File.separator + "saves" + File.separator + "mcmmo" + File.separator + "xp.json";

    // We are assuming that the player name is unique... this should work
    private Map<String/*displayName*/, PlayerXp> playerMap = new HashMap<String, PlayerXp>();

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
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs(); // create missing directories if necessary
        }
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

    /** Same as get with a player entity, but won't create a new entry if the playername wasn't found.
     *
     * @param playerName the player name for which to retrieve the xp object
     */
    public Optional<PlayerXp> get(String playerName) {
        if (playerMap.containsKey(playerName)) {
            return Optional.of(playerMap.get(playerName));
        } else {
            return Optional.empty();
        }
    }

    /** Returns the set of known player names
     */
    public Set<String> getPlayerNames() {
        return playerMap.keySet();
    }

    private void loadData() {
        System.out.println("Loading MCMMO data");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PlayerXp.class, PlayerXp.deserializer());
        Gson gson = builder.create();

        // load the save file if it already exists
        if (getSaveFile().exists()) {
            FileReader reader = null;
            try {
               reader = new FileReader(getSaveFile());
                playerMap = gson.fromJson(reader, new TypeToken<HashMap<String, PlayerXp>>(){}.getType());
                if (playerMap == null) {
                    playerMap = new HashMap<String, PlayerXp>();
                }
            } catch (IOException ex) {
                System.err.println("Failed to load MCMMO data. Error: " + ex);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch(IOException ex) {}
            }
        }
    }


    @SubscribeEvent
    public void onWorldSaved(WorldEvent.Save event) {
        //TODO how do I only save once and not thrice? The dimension-check doesn't work.

        // Save the player data using GSON
        System.out.println("Saving MCMMO data");
        Gson gson = new Gson();
        FileWriter writer = null;
        try {
            writer = new FileWriter(getSaveFile());
            writer.write(gson.toJson(playerMap));
        } catch (IOException ex) {
            System.err.println("Failed to save MCMMO data. Error: " + ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                System.err.println("Failed to save MCMMO data. Error: " + ex);
            }
        }
    }


}
