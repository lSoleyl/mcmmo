package lsoleyl.mcmmo.experience;

import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.utility.ChatFormat;

import java.util.Optional;

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
    public static final int TICKS_PER_SECOND = 20;


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

    /** Sets the skill on cooldown for the given amount of seconds.
     *
     * @param seconds
     */
    public void setCooldown(int seconds) {
        cooldownEnd = MCMMO.tickCount + TICKS_PER_SECOND * seconds;
    }


    @Override
    public String toString() {
        return ChatFormat.formatLevel(level) + " " + ChatFormat.formatLevelXp(getCurrentLevelXp(),getNextLevelXp());
    }
}
