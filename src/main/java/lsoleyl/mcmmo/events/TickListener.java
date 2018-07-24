package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lsoleyl.mcmmo.MCMMO;

public class TickListener {
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            // Simply count up the ticks... as needed for several cooldown calculations
            ++MCMMO.tickCount;
        }
    }
}
