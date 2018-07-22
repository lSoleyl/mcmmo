package lsoleyl.mcmmo.experience;

import static java.lang.Math.sqrt;

/** This class defines the progression curve and some methods to convert between xp and level.
 *  This class uses a singleton instance which precalculates a few values for faster calculations.
 *
 *  The base progression is required xp for a single level = 1000 + 20*level
 *  This yields in total xp needed for a given level = 1010*level + 10*level^2
 *
 *  This in turn gives the inverse of level = (sqrt(xp + 159.69^2) - 159.69) / 3.1622
 *
 *  We define 1000 as base and 20 slope, then we can redefine in more general terms:
 *  xp(level) = base + slope*level
 *  totalXp(level) = (base+slope/2)*level + (slope/2)*level^2
 *
 *  level(totalXp) = (sqrt(xp + z^2) - z) / sqrt(slope/2)
 *  with z = (base+(slope/2))/sqrt(2*slope)
*/
public class Progression {
    private static final Progression instance = new Progression();

    public final long base = 1000;
    public final long slope = 20; // must be dividable by 2 for the calculations to work correctly
    private final double z; // must be a double to correctly calculate th value.
    private final double z2;
    private final long halfSlope; // this is used frequently, so precalculate it
    private final double halfSlopeRoot;

    /** Precalculate a few values
     */
    private Progression() {
        z = (base+slope)/sqrt(2.0 * slope);
        z2 = z*z;
        halfSlope = slope / 2;
        halfSlopeRoot = sqrt(halfSlope);
    }

    public static Progression getInstance() {
        return instance;
    }

    /** Returns the level, which corresponds to the given total xp value.
     *
     * @param totalXp the xp to convert into the corresponding level
     * @return the calculated level
     */
    public int getLevel(long totalXp) {
        // rounding down is exactly what we want here
        int level = (int)((sqrt(totalXp + z2) - z) / halfSlopeRoot);

        // Now make sure, we don't have some kind of rounding mistake.
        // The formula should be off by at most one level.
        if (totalXp >= getTotalXp(level)) {
            if (totalXp >= getTotalXp(level+1)) {
                // the calculation returned one level below
                return level+1;
            } else {
                return level;
            }
        } else {
            // the calculation returned one level above
            return level-1;
        }

    }

    /** Returns the amount of xp needed to level up from level-1 to level
     *
     * @param level the level to reach from level-1
     * @return the required amount of xp
     */
    public long getLevelXp(int level) {
        return base + slope*level;
    }

    /** Returns the total amount of xp needed to reach a certain level from level 0
     *
     * @param level the level to reach from 0
     * @return the total amount of xp needed to each that level
     */
    public long getTotalXp(int level) {
        return (base + halfSlope)*level + halfSlope*level*level;
    }
}
