package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.skills.CooldownManager;
import lsoleyl.mcmmo.utility.Tuple;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedList;
import java.util.List;

public class TickListener {
    private final CooldownManager cooldownManager = CooldownManager.getInstance();

    // Not the best way to put it here, but well...
    public static List<Tuple<EntityPlayerMP, Integer/*potion id*/>> clearEffectList = new LinkedList<Tuple<EntityPlayerMP, Integer>>();

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            // Simply count up the ticks... as needed for several cooldown calculations
            ++MCMMO.tickCount;

            // Check ability cooldowns
            //TODO maybe we don't need to do this on every tick
            cooldownManager.checkCooldown();

            // Remove scheduled effects
            if (!clearEffectList.isEmpty()) {
                for (Tuple<EntityPlayerMP, Integer> entry : clearEffectList) {
                    entry.a.removePotionEffect(entry.b);
                }

                clearEffectList.clear();
            }
        }
    }
}
