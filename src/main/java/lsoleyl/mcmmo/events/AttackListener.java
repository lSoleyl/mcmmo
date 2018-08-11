package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.*;
import lsoleyl.mcmmo.utility.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

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
            DamageClassifier damage = DamageClassifier.classify(event.source);

            if (damage.isDodgeable) {
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
            } else if (damage.isArrow) {
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
            } else if (damage.isPoison || damage.isWither) {
                // Poison/wither cancel chance
                XPWrapper poison = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.POISON);

                if (damage.isPoison && targetPlayer.isPotionActive(Potion.poison) && !poison.isOnCooldown()) {
                    if (Rand.evaluate(PoisonSkill.poisonCancelChance.getValue(poison.getLevel()))) {
                        // We cannot remove the effect here, we need to wait until the next tick or else we get a
                        // ConcurrentModificationException as the server is currently iterating over the list of active potion effects to apply them.
                        TickListener.clearEffectList.add(new Tuple<EntityPlayerMP, Integer>(targetPlayer, Potion.poison.id));
                        poison.setCooldown(PoisonSkill.EFFECT_CANCEL_COOLDOWN);
                    }
                } else if (damage.isWither && targetPlayer.isPotionActive(Potion.wither) && !poison.isOnCooldown()) {
                    if (Rand.evaluate(PoisonSkill.witherCancelChance.getValue(poison.getLevel()))) {
                        // We cannot remove the effect here, we need to wait until the next tick or else we get a
                        // ConcurrentModificationException as the server is currently iterating over the list of active potion effects to apply them.
                        TickListener.clearEffectList.add(new Tuple<EntityPlayerMP, Integer>(targetPlayer, Potion.wither.id));
                        poison.setCooldown(PoisonSkill.EFFECT_CANCEL_COOLDOWN);
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

        DamageClassifier damage = DamageClassifier.classify(event.source);

        // Apply firefighting skills
        if (damage.isFire || damage.isExplosion) {
            if (targetPlayer != null) {
                // only evaluate this for players
                XPWrapper xp = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.FIREFIGHTING);

                if (damage.isFire) {
                    if (Rand.evaluate(FirefightingSkill.fireResistanceChance.getValue(xp.getLevel()))) {
                        event.ammount = 0.0f; // Cancel out all fire damage for this event
                    }
                } else {
                    // Reduce damage by explosion resistance
                    event.ammount -= FirefightingSkill.explosionDamageReduction.getValue(xp.getLevel());
                }

                // Now calculate the received experience from the burn damage
                int typeFactor = (damage.isExplosion) ? FirefightingSkill.EXPLOSION_XP_MULTIPLIER : 1;
                Optional<Integer> newLevel = xp.addXp((long) (event.ammount * FirefightingSkill.XP_PER_DAMAGE * typeFactor));
                MCMMO.playerLevelUp(targetPlayer, Skill.FIREFIGHTING, newLevel);
            }
        } else if (damage.isArrow) {

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
                int fireDuration = FirefightingSkill.fireArrowFireDuration.getValue(fireFighting.getLevel());
                if (event.entityLiving != null && fireDuration > 0) {
                    event.entityLiving.setFire(fireDuration);
                }

                // check whether we have to apply the wither effect from poison skill
                XPWrapper poison = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.POISON);
                int witherDuration = PoisonSkill.witherArrowDuration.getValue(poison.getLevel());
                if (event.entityLiving != null && witherDuration > 0) {
                    event.entityLiving.addPotionEffect(new PotionEffect(Potion.wither.id, witherDuration * XPWrapper.TICKS_PER_SECOND, 1, true));
                }
            }
        } else if(damage.isDrown) {
            if (targetPlayer != null) {
                // Drown damage -> diving skill
                XPWrapper diving = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.DIVING);

                if (Rand.evaluate(DivingSkill.airRestoreChance.getValue(diving.getLevel()))) {
                    // restore air
                    targetPlayer.setAir(300);
                }

                // now award xp for the remaining damage
                Optional<Integer> newLevel = diving.addXp((long) (DivingSkill.XP_PER_DAMAGE * event.ammount));
                MCMMO.playerLevelUp(targetPlayer, Skill.DIVING, newLevel);
            }
        } else if(damage.isFall) {
            if (targetPlayer != null) {
                // Fall damage -> parkour skill
                XPWrapper parkour = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.PARKOUR);


                int rollXp = 0; // The xp received from performing a roll

                // Sneaking is necessary to perform a roll
                if (targetPlayer.isSneaking()) {
                    // Reduce suffered damage by performing a roll
                    event.ammount -= event.ammount * ParkourSkill.parkourRollDamageReduction.getValue(parkour.getLevel());

                    // Check whether player has performed a graceful roll
                    if (Rand.evaluate(ParkourSkill.perfectRollChance.getValue(parkour.getLevel()))) {
                        // Apply speed potion effect
                        targetPlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, ParkourSkill.SPEED_BOOST_DURATION * XPWrapper.TICKS_PER_SECOND, 1, true));
                        event.ammount = 0;
                        rollXp = ParkourSkill.PERFECT_ROLL_XP;
                    } else {
                        rollXp = ParkourSkill.ROLL_XP;
                    }

                    // This comes very close to a landing sound
                    Sound.HORSE_LAND.playAt(targetPlayer);
                }

                // award player xp for suffered fall damage and performed rolls
                Optional<Integer> newLevel = parkour.addXp((long) (ParkourSkill.XP_PER_DAMAGE * event.ammount + rollXp));
                MCMMO.playerLevelUp(targetPlayer, Skill.PARKOUR, newLevel);
            }
        } else if (targetPlayer != null && (damage.isPoison || damage.isWither)) {
            // Effect cancel chance has already been evaluated so just reward the player for the suffered damage
            int XP_PER_DAMAGE = 0;
            if (damage.isPoison && targetPlayer.isPotionActive(Potion.poison)) {
                XP_PER_DAMAGE = PoisonSkill.XP_PER_DAMAGE;
            } else if (damage.isWither && targetPlayer.isPotionActive(Potion.wither)) {
                XP_PER_DAMAGE = PoisonSkill.XP_PER_DAMAGE * PoisonSkill.WITHER_MULTIPLIER;
            }

            // Level up the skill
            XPWrapper poison = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.POISON);
            Optional<Integer> newLevel = poison.addXp((long) (XP_PER_DAMAGE * event.ammount));
            MCMMO.playerLevelUp(targetPlayer, Skill.POISON, newLevel);
        } else if (damage.isDodgeable) {
            // Regular combat damage
            if (sourcePlayer != null) {
                // Apply combat damage increasing skills
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
                } else if (Tools.isAxe(sourcePlayer.getHeldItem())){
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
                } else if (Tools.isSword(sourcePlayer.getHeldItem())) {
                    XPWrapper swords = MCMMO.getPlayerXp(sourcePlayer).getSkillXp(Skill.SWORDS);

                    // perform ability activation check and apply clean cutter if it is active
                    if (swords.checkedActivateAbility(SwordsSkill.CLEAN_CUTTER_DURATION)) {
                        event.ammount *= SwordsSkill.cleanCutterDamageMultiplier.getValue(swords.getLevel());
                    }


                    // Now apply the poisoning effects
                    if (Rand.evaluate(SwordsSkill.poisonChance.getValue(swords.getLevel()))) {
                        int poisonDuration = SwordsSkill.poisonDuration.getValue(swords.getLevel()) * XPWrapper.TICKS_PER_SECOND;
                        int poisonPotency = SwordsSkill.poisonPotency.getValue(swords.getLevel());

                        event.entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, poisonDuration, poisonPotency, true));
                    }

                    // Set combat skill to reward xp after reducing damage
                    combatSkill = swords;
                }
            }

            // Apply damage reduction of target player's combat skill
            if (targetPlayer != null) {
                // Actual combat damage -> reduce by combat skill
                XPWrapper combat = MCMMO.getPlayerXp(targetPlayer).getSkillXp(Skill.COMBAT);
                event.ammount -= CombatSkill.damageReduction.getValue(combat.getLevel());

                // now award xp for the remaining damage
                Optional<Integer> newLevel = combat.addXp((long) (CombatSkill.XP_PER_DAMAGE * event.ammount));
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
        int XP_PER_DAMAGE = 50;

        if (skillXp.getSkill() == Skill.UNARMED) {
            // Unarmed skill is way too hard to level if the xp reward isn't increased.
            XP_PER_DAMAGE *= 2;
        }

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
}
