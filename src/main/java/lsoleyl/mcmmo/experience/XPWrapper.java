package lsoleyl.mcmmo.experience;

import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.Optional;

/** This class wraps the PlayerXP object for a specific skill an exposes better interface for reading and modifying
 *  the value. For better performance each object keeps the current xp value and level cached in a local field so
 *  it is not advised to create multiple such objects for the same skill.
 */
public class XPWrapper {
    private Skill skill;
    private PlayerXp xp;
    private long xpValue; // cache the value in a local field for faster access
    private int level;    // the current level this gets updated upon construction of this wrapper and when adding xp

    // since we have only one XPWrapper per player per skill, we can store the skill's cooldown in this object.
    private long cooldownEnd = 0;
    private long abilityEnd = 0; // if an ability is active, then this will hold the time after which it runs out
    private long abilityPrepareEnd = 0; // for activation abilities like berserk the time after which it automatically disables again.
    public static final int TICKS_PER_SECOND = 20;
    public static final int PREPARE_TIMEOUT = 3; //s
    public static final int ABILITY_COOLDOWN = 120; //s


    public XPWrapper(PlayerXp xp, Skill skill) {
        this.xp = xp;
        this.skill = skill;
        xpValue = xp.get(skill);
        level = Progression.getInstance().getLevel(xpValue);
    }

    /** Adds the given xpAmount to this skill. This change is immediately applied to the PlayerXp object.
     *
     * @param xpAmount the amount of xp to add. If this value is negative, no change is applied to the player's xp
     * @return if the xp has caused the level to change, the new level is returned.
     */
    public Optional<Integer> addXp(long xpAmount) {
        if (xpAmount <= 0) {
            return Optional.empty();
        }

        xpValue += xpAmount;
        xp.set(skill, xpValue);

        int prevLevel = level;

        while (getRemainingLevelXp() <= 0) {
            ++level;
        }

        if (level > prevLevel) {
            return Optional.of(level);
        } else {
            return Optional.empty();
        }
    }

    /** Returns the currently collected xp towards the next level.
     *  This value is always smaller than  getLevelXp(level+1)
     *
     * @return
     */
    public long getCurrentLevelXp() {
        return xpValue - Progression.getInstance().getTotalXp(level);
    }

    /** Returns the total amount of xp, the player needs to collect to reach the next level from the current level
     */
    public long getNextLevelXp() {
        return Progression.getInstance().getLevelXp(level+1);
    }

    /** Returns the remaining xp to reach the next level
     */
    public long getRemainingLevelXp() {
        return Progression.getInstance().getTotalXp(level+1) - xpValue;
    }

    /** Returns the current level of this skill
     */
    public int getLevel() {
        return level;
    }

    /** Returns true if the skill is still on cooldown
     */
    public boolean isOnCooldown() {
        return MCMMO.tickCount < cooldownEnd;
    }

    /** Returns the remaining cooldown in seconds
     */
    public int getRemainingCooldown() { return (int) ((cooldownEnd - MCMMO.tickCount) / TICKS_PER_SECOND); }

    /** Sets the skill on cooldown for the predefined constant time of 120 seconds.
     */
    public void setCooldown() {
        setCooldown(ABILITY_COOLDOWN);
    }

    /** Sets the skill on cooldown for the given amount of seconds
     *
     * @param cooldown the cooldown to apply to the skill's ability
     */
    public void setCooldown(int cooldown) {
        cooldownEnd = MCMMO.tickCount + TICKS_PER_SECOND * cooldown;
    }

    /** Returns true if the skill's ability is currently active
     */
    public boolean isAbilityActive() { return MCMMO.tickCount < abilityEnd; }

    /** Sets the skill's ability to active for a given number of seconds
     *
     * @param seconds the number of seconds after which the ability runs out
     */
    private void activateAbility(int seconds) {
        abilityEnd = MCMMO.tickCount + TICKS_PER_SECOND * seconds;
        abilityPrepareEnd = 0; // with ability activation the skill is no longer prepared
    }


    /** Checks whether the ability can be activated and activates it for the given number of seconds and
     *  also sets the ability's cooldown
     *
     * @param abilityDuration the number of seconds, the ability should be activated for.
     *
     * @return true if the ability was already active or has now been activated
     */
    public boolean checkedActivateAbility(int abilityDuration) {
        // Check whether we have to activate berserk
        if (!isAbilityActive() && isAbilityPrepared()) {
            activateAbility(abilityDuration);
            setCooldown();
        }

        return isAbilityActive();
    }

    /** Returns true if the user has prepared the given ability
     */
    public boolean isAbilityPrepared() { return MCMMO.tickCount < abilityPrepareEnd; }

    /** Prepare activation of ability the ability will be in prepared state for 3 seconds
     */
    public void prepareAbility() { abilityPrepareEnd = MCMMO.tickCount + TICKS_PER_SECOND * PREPARE_TIMEOUT; }

    /** Cancels preparing the ability
     */
    public void cancelPrepare() { abilityPrepareEnd = 0; }

    public Skill getSkill() { return skill; }


    @Override
    public String toString() {
        return ChatFormat.formatLevel(level) + " " + ChatFormat.formatLevelXp(getCurrentLevelXp(),getNextLevelXp());
    }
}
