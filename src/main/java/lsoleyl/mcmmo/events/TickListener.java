package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.skills.CooldownManager;

public class TickListener {
    private final CooldownManager cooldownManager = CooldownManager.getInstance();

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            // Simply count up the ticks... as needed for several cooldown calculations
            ++MCMMO.tickCount;

            // Check ability cooldowns
            //TODO maybe we don't need to do this on every tick
            cooldownManager.checkCooldown();
        }
    }
}
