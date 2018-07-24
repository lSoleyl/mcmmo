package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.CooldownManager;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.skills.SkillRegistry;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/** Listens on right click events to prepare abilities and register them to the cooldown manager
 */
public class AbilityListener {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        // this sadly doesn't get fired if the user has no item in hand and right clicks into the air, so
        // we have to resort to shift-right click for this action... this should be unproblematic, as shift
        // right click is mostly used with items
        if (event.entityPlayer.getHeldItem() == null) {
            if (event.entityPlayer.isSneaking()) {
                EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
                XPWrapper unarmed = MCMMO.getPlayerXp(player).getSkillXp(Skill.UNARMED);
                if (unarmed.isAbilityPrepared()) {
                    unarmed.cancelPrepare();  // lower arms
                } else if (!unarmed.isAbilityActive()) {
                    if (unarmed.isOnCooldown()) {
                        new ChatWriter(player).writeMessage(ChatFormat.formatCooldown("Berserk", unarmed.getRemainingCooldown()));
                    } else {
                        // prepare ability (register will set the abilility to prepared)
                        CooldownManager.getInstance().register(player, unarmed, SkillRegistry.getInstance().UNARMED);
                    }
                }
            }
        } else {
            // The player holds an item in his hand... check that item type

        }
    }

    //@SubscribeEvent
    public void onPlayerPunchEvent() {
        //
    }
}
