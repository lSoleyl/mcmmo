package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.CombatSkill;
import lsoleyl.mcmmo.skills.FirefightingSkill;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import lsoleyl.mcmmo.utility.Rand;
import lsoleyl.mcmmo.utility.Sound;
import net.minecraft.block.Block;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Optional;

public class AttackListener {

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP target = (EntityPlayerMP) event.entityLiving;

            if (!event.source.isProjectile() && !event.source.isFireDamage() && !event.source.isExplosion() && !event.source.isMagicDamage()) {
                // Seems to be regular combat damage
                XPWrapper combat = MCMMO.getPlayerXp(target).getSkillXp(Skill.COMBAT);

                if (!combat.isOnCooldown() && Rand.evaluate(CombatSkill.dodgeChance.getValue(combat.getLevel()))) {
                    // Successful dodge... print short info and award xp and cancel the event
                    new ChatWriter(target).writeMessage(ChatFormat.formatEffectActivated("Dodge"));


                    Optional<Integer> newLevel = combat.addXp(CombatSkill.DODGE_XP);
                    MCMMO.playerLevelUp(target, Skill.COMBAT, newLevel);

                    combat.setCooldown(CombatSkill.DODGE_COOLDOWN);
                    event.setCanceled(true);

                    Sound.WOOD_CLICK.playAt(target);
                }
            }
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        EntityPlayerMP targetPlayer = null;
        if (event.entity instanceof EntityPlayerMP) {
            targetPlayer = (EntityPlayerMP) event.entity;
        }

        EntityPlayerMP sourcePlayer = null;
        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayerMP) {
            sourcePlayer = (EntityPlayerMP) event.source.getEntity();
        }


        // Apply firefighting skills
        if (event.source.isFireDamage() || event.source.isExplosion()) {
            if (targetPlayer != null) {
                // only evaluate this for players
                XPWrapper xp = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.FIREFIGHTING);

                if (event.source.isFireDamage()) {
                    if (Rand.evaluate(FirefightingSkill.fireResistanceChance.getValue(xp.getLevel()))) {
                        event.ammount = 0.0f; // Cancel out all fire damage for this event
                    }
                } else {
                    // Reduce damage by explosion resistance
                    event.ammount -= FirefightingSkill.explosionDamageReduction.getValue(xp.getLevel());
                }

                // Now calculate the received experience from the burn damage
                int typeFactor = (event.source.isExplosion()) ? FirefightingSkill.EXPLOSION_XP_MULTIPLIER : 1;
                Optional<Integer> newLevel = xp.addXp((long) (event.ammount * FirefightingSkill.XP_PER_DAMAGE * typeFactor));
                MCMMO.playerLevelUp(targetPlayer, Skill.FIREFIGHTING, newLevel);
            }
        } else if (event.source.isProjectile() && event.source.getSourceOfDamage() instanceof EntityArrow) {
            //TODO evaluate archery skills

            if (event.source.getEntity() instanceof EntityPlayerMP) {
                sourcePlayer = (EntityPlayerMP) event.source.getEntity();
            }



            //TODO check whether arrow can be caught -> if so add to inventory and cancel all damage and don't continue

            // check whether we have to apply fire effect
            if (sourcePlayer != null) {
                XPWrapper fireFighting = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.FIREFIGHTING);
                if (event.entityLiving != null) {
                    event.entityLiving.setFire(FirefightingSkill.fireArrowFireDuration.getValue(fireFighting.getLevel()));
                }
            }
        } else if (!event.source.isMagicDamage()) {
            // Regular combat damage

            //TODO apply other combat skills depending on the sourcePlayer's currently equipped tool (classification needed)

            // Apply damage reduction of target player's combat skill
            if (targetPlayer != null) {
                XPWrapper xp = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.COMBAT);
                event.ammount -= CombatSkill.damageReduction.getValue(xp.getLevel());

                // now award xp for the remaining damage
                Optional<Integer> newLevel = xp.addXp((long) (CombatSkill.XP_PER_DAMAGE * event.ammount));
                MCMMO.playerLevelUp(targetPlayer, Skill.COMBAT, newLevel);
            }
        }


        //TODO this event is being evaluated to determine the actual damage to apply to the armor and health of the entity
        //TODO so we need to check source and target and possibly apply any relevant combat skills
        //TODO we also have to differentiate between mobs and players
    }

    public String print(EntityPlayer player) {
        return "Player(name=" + player.getDisplayName() + ",xp=" + MCMMO.getPlayerXp(player) + ",currentItem=" + print(player.getCurrentEquippedItem()) + ",usingItem=" + player.isUsingItem() + ")";

    }

    public String print(ItemStack item) {
        String names = "";
        for(int id : OreDictionary.getOreIDs(item)) {
            names += OreDictionary.getOreName(id) + ",";
        }

        //TODO the unlocalized name seems to always contain sword for swords... etc.
        return "Item(dispName=" + item.getDisplayName() + ",unlocalName=" + item.getUnlocalizedName() + ",names=[" + names + "])";
    }


    @SubscribeEvent
    public void itemUsed(PlayerUseItemEvent.Finish event) { //Cannot use PlayerUseItemEvent (it's abstract)
        System.out.println(print(event.entityPlayer) + " used item " + event.item);
    }

}
