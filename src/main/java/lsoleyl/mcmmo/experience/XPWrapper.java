package lsoleyl.mcmmo.experience;

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


    public XPWrapper(PlayerXp xp, Skill skill) {
        this.xp = xp;
        this.skill = skill;
        xpValue = xp.get(skill);
        level = Progression.getInstance().getLevel(xpValue);
    }

    /** Adds the given xpAmount to this skill. This change is immediately applied to the PlayerXp object.
     *
     * @param xpAmount the amount of xp to add.
     * @return if the xp has caused the level to change, the new level is returned.
     */
    public Optional<Integer> addXp(long xpAmount) {
        xpValue += xpAmount;
        xp.set(skill, xpValue);

        if (getRemainingLevelXp() <= 0) {
            level = Progression.getInstance().getLevel(xpValue);
            return Optional.of(level);
        } else {
            return Optional.empty();
        }
    }

    /** Returns the current total xp value
     */
    public long getTotalXp() {
        return xpValue;
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


    @Override
    public String toString() {
        return ChatFormat.formatLevel(level) + " " + ChatFormat.formatLevelXp(getCurrentLevelXp(),getNextLevelXp());
    }
}
