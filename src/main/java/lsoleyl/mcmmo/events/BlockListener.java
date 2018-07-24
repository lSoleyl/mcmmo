package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.BlockEvent;

public class BlockListener {
    //@SubscribeEvent Ignore the event for now...
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        System.out.println("Player " + player + " has broken the block " + event.block);

        //TODO we have to determine, which tool was used to break the block
        //TODO pass this info somehow to our MCMMO-logic to evaluate the gained XP and potential drops
    }
}
