package lsoleyl.mcmmo.data;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStorage {

    private Map<UUID/*playerUid*/, PlayerXp> playerMap = new HashMap<>();
    //TODO fetch the data associated with a certain player


    private DataStorage() {}

    public static DataStorage Initialize() {
        //TODO load player data from some kind of data storage

        return new DataStorage();
    }

    /** Retrieves the xp state for the given player entity. If the player is not yet known to
     *  the datastorage, a new playerXp object is created for that player.
     *
     * @param player the player to retrieve the data for
     * @return the player's xp data, which may be manipulated
     */
    public PlayerXp get(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        if (!playerMap.containsKey(uuid)) {
            playerMap.put(uuid, new PlayerXp(player));
        }

        return playerMap.get(uuid);
    }




    @SubscribeEvent
    public void onWorldSaved(WorldEvent.Save event) {
        //TODO save data back into the storage file
    }


}
