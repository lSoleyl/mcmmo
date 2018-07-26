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
import net.minecraft.item.ItemAxe;
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
                        // prepare ability (register will set the ability to prepared)
                        CooldownManager.getInstance().register(player, unarmed, SkillRegistry.getInstance().UNARMED);
                    }
                }
            }
        } else if (event.entityPlayer.getHeldItem().getItem() instanceof ItemAxe) {
            // Skull splitter
            if (event.entityPlayer.isSneaking()) {
                EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
                XPWrapper axes = MCMMO.getPlayerXp(player).getSkillXp(Skill.AXES);
                if (axes.isAbilityPrepared()) {
                    axes.cancelPrepare();  // lower axe
                } else if (!axes.isAbilityActive()) {
                    if (axes.isOnCooldown()) {
                        new ChatWriter(player).writeMessage(ChatFormat.formatCooldown("Skull splitter", axes.getRemainingCooldown()));
                    } else {
                        // prepare ability (register will set the ability to prepared)
                        CooldownManager.getInstance().register(player, axes, SkillRegistry.getInstance().AXES);
                    }
                }
            }
        }
    }
}
