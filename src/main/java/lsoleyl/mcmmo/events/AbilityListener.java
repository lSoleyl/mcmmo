package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.CooldownManager;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.skills.SkillRegistry;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import lsoleyl.mcmmo.utility.Entities;
import lsoleyl.mcmmo.utility.Tools;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/** Listens on right click events to prepare abilities and register them to the cooldown manager
 */
public class AbilityListener {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            // In SP this listener gets called by the client side logic aswell
            return;
        }

        // Make sure we apply this to a real player, not to a turtle
        final EntityPlayerMP player = Entities.getPlayer(event.entityPlayer);
        if (player == null) {
            return;
        }

        // this sadly doesn't get fired if the user has no item in hand and right clicks into the air, so
        // we have to resort to shift-right click for this action... this should be unproblematic, as shift
        // right click is mostly used with items
        if (player.getHeldItem() == null) {
            if (player.isSneaking()) {
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
        } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            // If we are holding a tool, we aren't interested in right clicking a block... With the tool we can also right
            // click in the air. But if we don't ignore the block right click then we would receive two events and directly
            // cancel the prepare.

            if (Tools.isAxe(player.getHeldItem())) {
                // Skull splitter
                if (player.isSneaking()) {
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
            } else if (Tools.isSword(player.getHeldItem())) {
                // Clean cutter
                if (player.isSneaking()) {
                    XPWrapper swords = MCMMO.getPlayerXp(player).getSkillXp(Skill.SWORDS);
                    if (swords.isAbilityPrepared()) {
                        swords.cancelPrepare();  // lower sword
                    } else if (!swords.isAbilityActive()) {
                        if (swords.isOnCooldown()) {
                            new ChatWriter(player).writeMessage(ChatFormat.formatCooldown("Clean cutter", swords.getRemainingCooldown()));
                        } else {
                            // prepare ability (register will set the ability to prepared)
                            CooldownManager.getInstance().register(player, swords, SkillRegistry.getInstance().SWORDS);
                        }
                    }
                }
            }
        }
    }
}
