package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.*;
import lsoleyl.mcmmo.utility.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Optional;

public class AttackListener {
    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        EntityPlayerMP targetPlayer = null;
        if (event.entity instanceof EntityPlayerMP) {
            targetPlayer = (EntityPlayerMP) event.entity;
        }

        EntityPlayerMP sourcePlayer = null;
        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayerMP) {
            sourcePlayer = (EntityPlayerMP) event.source.getEntity();
        }

        if (targetPlayer != null) {
            if (!event.source.isProjectile() && !event.source.isFireDamage() && !event.source.isExplosion() && !event.source.isMagicDamage()) {
                // Seems to be regular combat damage
                XPWrapper combat = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.COMBAT);

                if (!combat.isOnCooldown() && Rand.evaluate(CombatSkill.dodgeChance.getValue(combat.getLevel()))) {
                    // Successful dodge... print short info and award xp and cancel the event
                    new ChatWriter(targetPlayer).writeMessage(ChatFormat.formatEffectActivated("Dodge"));


                    Optional<Integer> newLevel = combat.addXp(CombatSkill.DODGE_XP);
                    MCMMO.playerLevelUp(targetPlayer, Skill.COMBAT, newLevel);

                    combat.setCooldown(CombatSkill.DODGE_COOLDOWN);
                    event.setCanceled(true);

                    Sound.WOOD_CLICK.playAt(targetPlayer);
                }
            } else if (event.source.isProjectile() && event.source.getSourceOfDamage() instanceof EntityArrow) {
                // Evaluate catch and curve shot chance
                XPWrapper archery = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.ARCHERY);
                if (Rand.evaluate(ArcherySkill.catchChance.getValue(archery.getLevel()))) {
                    // arrow has been caught... but only if the source player's curve shot doesn't trigger
                    XPWrapper sourceArchery = null;
                    if (sourcePlayer != null) {
                        sourceArchery = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.ARCHERY);
                    }

                    if (sourceArchery != null && Rand.evaluate(ArcherySkill.curveShotChance.getValue(sourceArchery.getLevel()))) {
                        // Arrow couldn't be caught... the curve shot slipped through -> continue to onHurt(), but
                        // don't remember that this was a curve shot... It doesn't need to also deal twice the damage
                    } else {
                        // Arrow has been caught -> award xp, cancel event and add arrow to player's inventory

                        Optional<Integer> newLevel = archery.addXp(ArcherySkill.CATCH_XP);
                        MCMMO.playerLevelUp(targetPlayer, Skill.ARCHERY, newLevel);

                        event.setCanceled(true);
                        Sound.POP.playAt(targetPlayer);

                        // We have to remove the arrow entity from the world to prevent it from dropping down after cancelling the event.
                        EntityArrow source = (EntityArrow) event.source.getSourceOfDamage();
                        source.worldObj.removeEntity(source);

                        targetPlayer.inventory.addItemStackToInventory(new ItemStack((Item) Item.itemRegistry.getObject("minecraft:arrow")));
                    }
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

            // Increase damage after we have made sure, the arrow hasn't been caught (happens in onAttack())
            if (sourcePlayer != null) {
                XPWrapper archery = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.ARCHERY);
                event.ammount *= 1.0 + ArcherySkill.skillShotDamage.getValue(archery.getLevel());

                // now evaluate the chance of a curve shot
                if (Rand.evaluate(ArcherySkill.curveShotChance.getValue(archery.getLevel()))) {
                    event.ammount *= ArcherySkill.CURVE_SHOT_DAMAGE_FACTOR;
                }

                // reward xp to shooting player proportional to damage but only for hostile mobs and at most the mob's health
                rewardXpByTargetDamage(event, sourcePlayer, archery, ArcherySkill.XP_PER_DAMAGE);


                // check whether we have to apply fire effect from firefighting skill
                XPWrapper fireFighting = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.FIREFIGHTING);
                if (event.entityLiving != null) {
                    event.entityLiving.setFire(FirefightingSkill.fireArrowFireDuration.getValue(fireFighting.getLevel()));
                }
            }
        } else if (!event.source.isMagicDamage()) {
            // Regular combat damage
            if (sourcePlayer != null) {
                // Apply combat damage increasing skills

                //TODO detect the player's equipped item
                if (sourcePlayer.getHeldItem() == null) {
                    // Unarmed
                    XPWrapper unarmed = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.UNARMED);
                    event.ammount += UnarmedSkill.ironArmDamage.getValue(unarmed.getLevel());

                    // evaluate disarm chance (only applies to players)
                    if (targetPlayer != null) {
                        if (Rand.evaluate(UnarmedSkill.disarmChance.getValue(unarmed.getLevel()))) {
                            // target player has a chance to cancel this
                            if (Rand.evaluate(UnarmedSkill.ironGripChance.getValue(MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.UNARMED).getLevel()))) {
                                // target player has prevented being disarmed
                            } else {
                                // we need to manually play the drop sound
                                Sound.POP.playAt(targetPlayer);
                                targetPlayer.dropOneItem(true/*drop whole stack*/);
                            }
                        }
                    }

                    // Check whether we have to activate berserk
                    if (!unarmed.isAbilityActive() && unarmed.isAbilityPrepared()) {
                        unarmed.activateAbility(UnarmedSkill.berserkDuration.getValue(unarmed.getLevel()));
                        unarmed.setCooldown(UnarmedSkill.BERSERK_COOLDOWN);
                    }

                    // Evaluate additional berserk damage
                    if (unarmed.isAbilityActive()) {
                        event.ammount *= UnarmedSkill.BERSERK_DAMAGE_MULTIPLIER;
                    }

                    // Convert dealt damage to xp
                    rewardXpByTargetDamage(event, sourcePlayer, unarmed, UnarmedSkill.XP_PER_DAMAGE);
                } else {
                    //TODO categorize item in use and select skill depending on the item type
                }
            }


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

    private void rewardXpByTargetDamage(LivingHurtEvent event, EntityPlayerMP sourcePlayer, XPWrapper skillXp, int xpPerDamage) {
        // Only reward xp for damaging potentially dangerous entities
        if (!Entities.isPeacefulTowards(event.entity, sourcePlayer)) {
            // now convert the generated damage into xp (cannot exceed the entities hp)
            // I know, this calculation is a bit flawed, because the armor is not being applied, but it still
            // caps the xp per hit to a reasonable maximum.
            float effectiveDamage = Math.min(event.entityLiving.getHealth(), event.ammount);
            Optional<Integer> newLevel = skillXp.addXp((long) (xpPerDamage * effectiveDamage));
            MCMMO.playerLevelUp(sourcePlayer, skillXp.getSkill(), newLevel);
        }
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
}
