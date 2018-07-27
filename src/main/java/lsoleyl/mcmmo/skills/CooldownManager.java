package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.experience.XPWrapper;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** This singleton class manages active ability cooldowns and notifies the skills about timed out skills etc.
 */
public class CooldownManager {
    private static final CooldownManager instance = new CooldownManager();
    private List<CooldownEntry> cooldownEntries = new LinkedList<CooldownEntry>();

    public static CooldownManager getInstance() {
        return instance;
    }


    /** Register a skill to the cooldown manager to notify the skill object about skill state changes.
     *  This must be called with an inactive skill. Or else an exception will be thrown.
     *  If the ability wears out or the preparation times out, the corresponding list entry will be automatically removed.
     *  Upon registration onPrepare gets called on the passed skillObject.
     *
     * @param player the player, which wants to activate the ability
     * @param skillXp the skill object, which holds the current ability state (unprepared/inactive)
     * @param skillObject the instance of the skill to notify about ability state changes
     */
    public void register(EntityPlayerMP player, XPWrapper skillXp, IAbilitySkill skillObject) {
        if (skillXp.isAbilityActive()) {
            throw new RuntimeException("The passed skill object is in an illegal state... Ability must be inactive.");
        }

        if (!skillXp.isAbilityPrepared()) {
            skillXp.prepareAbility();
        }

        // fire this event regardless of whether the skill was already prepared... these hooks are only to be called by this class.
        skillObject.onPrepare(player);


        cooldownEntries.add(new CooldownEntry(player, skillXp, skillObject));
    }


    /** Should get called each tick to check skills for timeout
     *
     */
    public void checkCooldown() {
        // use a list iterator to be able to remove entries from the list
        ListIterator<CooldownEntry> iterator = cooldownEntries.listIterator();
        while(iterator.hasNext()) {
            CooldownEntry entry = iterator.next();
            if (entry.prepared && !entry.skillXp.isAbilityPrepared()) {
                if (entry.skillXp.isAbilityActive()) {
                    // ability has been activated
                    entry.prepared = false;
                    entry.activated = true;

                    entry.skillObject.onAbilityActivate(entry.player);
                } else {
                    // preparation has worn out -> remove entry
                    entry.skillObject.onPrepareTimeout(entry.player);
                    iterator.remove();
                }
            } else if (entry.activated && !entry.skillXp.isAbilityActive()) {
                // ability has worn out -> remove entry
                entry.skillObject.onAbilityWearOut(entry.player);
                iterator.remove();
            }
        }
        
    }




    private class CooldownEntry {
        final EntityPlayerMP player;
        final XPWrapper skillXp;
        final IAbilitySkill skillObject;

        boolean activated = false;
        boolean prepared = true;


        private CooldownEntry(EntityPlayerMP player, XPWrapper skillXp, IAbilitySkill skillObject) {
            this.player = player;
            this.skillXp = skillXp;
            this.skillObject = skillObject;
        }
    }
}
