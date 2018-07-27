package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.*;
import lsoleyl.mcmmo.utility.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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

                    // Apply a mild knock back to not make the dodge useless by simply spamming attacks
                    // This sadly only works for mobs... not for players
                    if (event.source.getEntity() instanceof EntityLivingBase) {
                        Entities.knockBack(targetPlayer, (EntityLivingBase) event.source.getEntity());
                    }
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

    private boolean processOnHurt = true;

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (!processOnHurt) {
            // For handling generated onHurt-Events caused by AOE-Damage of skull splitter
            return;
        }

        EntityPlayerMP targetPlayer = null;
        if (event.entity instanceof EntityPlayerMP) {
            targetPlayer = (EntityPlayerMP) event.entity;
        }

        EntityPlayerMP sourcePlayer = null;
        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayerMP) {
            sourcePlayer = (EntityPlayerMP) event.source.getEntity();
        }

        // This is set to the source player's skill xp wrapper if the attack is a regular close combat attack
        // to award him for the remaining damage dealt AFTER the target player's combat skill has been evaluated and
        // the damage reduced
        XPWrapper combatSkill = null;


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
                rewardXpByTargetDamage(event, sourcePlayer, archery);


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

                    // perform ability activation check and apply berserk damage if it is active
                    if (unarmed.checkedActivateAbility(UnarmedSkill.berserkDuration.getValue(unarmed.getLevel()))) {
                        event.ammount *= UnarmedSkill.BERSERK_DAMAGE_MULTIPLIER;
                    }

                    // Set combat skill to reward xp after reducing damage
                    combatSkill = unarmed;
                } else if (sourcePlayer.getHeldItem().getItem() instanceof ItemAxe){
                    // let's hope, all modded axes are derived form ItemAxe

                    XPWrapper axes = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.AXES);

                    // apply additional damage from axe master
                    event.ammount += AxesSkill.axeMasterDamage.getValue(axes.getLevel());

                    // evaluate critical strikes
                    if (Rand.evaluate(AxesSkill.criticalChance.getValue(axes.getLevel()))) {
                        event.ammount *= 2;
                    }

                    // ArmorImpact
                    if (targetPlayer != null && AxesSkill.armorImpactDamage.getValue(axes.getLevel()) > 0) {
                        // We have to multiply the damage by 4 as the damage gets divided by 4 before being applied as damage to the armor.
                        targetPlayer.inventory.damageArmor(AxesSkill.armorImpactDamage.getValue(axes.getLevel())*4);
                    }


                    // perform ability activation check and apply skull splitter if it is active
                    if (axes.checkedActivateAbility(AxesSkill.skullSplitterDuration.getValue(axes.getLevel()))) {
                        // disable hurt processing as it would lead to an endless recursion
                        processOnHurt = false;
                        for (Object obj : Entities.getEntitiesAround(event.entity, 1)) {
                            if (obj instanceof EntityLiving && obj != sourcePlayer) {
                                // Reuse same event source as it is still valid for the AOE damage, just adapt the damage itself.
                                ((EntityLiving) obj).attackEntityFrom(event.source, event.ammount / 2);
                            }
                        }
                        processOnHurt = true;
                    }

                    // Set combat skill to reward xp after reducing damage
                    combatSkill = axes;
                } else if (sourcePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    XPWrapper swords = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.SWORDS);

                    // perform ability activation check and apply clean cutter if it is active
                    if (swords.checkedActivateAbility(SwordsSkill.CLEAN_CUTTER_DURATION)) {
                        event.ammount *= SwordsSkill.cleanCutterDamageMultiplier.getValue(swords.getLevel());
                    }


                    // Now apply the poisoning effects
                    if (Rand.evaluate(SwordsSkill.poisonChance.getValue(swords.getLevel()))) {
                        int poisonDuration = SwordsSkill.poisonDuration.getValue(swords.getLevel()) * XPWrapper.TICKS_PER_SECOND;
                        int poisonPotency = SwordsSkill.poisonPotency.getValue(swords.getLevel());

                        event.entityLiving.addPotionEffect(new PotionEffect(Potion.poison.getId(), poisonDuration, poisonPotency, true));
                    }

                    // Set combat skill to reward xp after reducing damage
                    combatSkill = swords;
                }
            }

            // Apply damage reduction of target player's combat skill
            if (targetPlayer != null) {
                XPWrapper xp = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.COMBAT);
                event.ammount -= CombatSkill.damageReduction.getValue(xp.getLevel());

                // now award xp for the remaining damage
                Optional<Integer> newLevel = xp.addXp((long) (CombatSkill.XP_PER_DAMAGE * event.ammount));
                MCMMO.playerLevelUp(targetPlayer, Skill.COMBAT, newLevel);
            }

            // After we have reduced the damage by the target player's combat skill, we can convert the remaining dealt damage
            // to xp... That way farming xp by attacking a player with high combat skill is not possible.
            if (combatSkill != null) {
                rewardXpByTargetDamage(event, sourcePlayer, combatSkill);
            }
        }
    }

    private void rewardXpByTargetDamage(LivingHurtEvent event, EntityPlayerMP sourcePlayer, XPWrapper skillXp) {
        // Use a constant Damage -> XP conversion for all combat skills
        final int XP_PER_DAMAGE = 50;

        // Only reward xp for damaging potentially dangerous entities
        if (!Entities.isPeacefulTowards(event.entity, sourcePlayer)) {
            // now convert the generated damage into xp (cannot exceed the entities hp)
            // I know, this calculation is a bit flawed, because the armor is not being applied, but it still
            // caps the xp per hit to a reasonable maximum.
            float effectiveDamage = Math.min(event.entityLiving.getHealth(), event.ammount);
            Optional<Integer> newLevel = skillXp.addXp((long) (XP_PER_DAMAGE * effectiveDamage));
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
