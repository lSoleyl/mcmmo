package lsoleyl.mcmmo.skills.progression;

/** This class represents a linear probability progression
 *
 */
public class LinearProbabilityProgression {
    public final int startLevel;
    public final double startValue;
    public final double incrementPerLevel;
    public final double maxValue;


    public LinearProbabilityProgression(int startLevel, double startValue, double incrementPerLevel, double maxValue) {
        this.startLevel = startLevel;
        this.startValue = startValue;
        this.incrementPerLevel = incrementPerLevel;
        this.maxValue = maxValue;
    }

    public LinearProbabilityProgression(double incrementPerLevel) {
        this(0, 0,incrementPerLevel, 1.0);
    }

    public LinearProbabilityProgression(double incrementPerLevel, double maxValue) {
        this(0, 0,incrementPerLevel, maxValue);
    }


    public double getValue(int level) {
        if (level < startLevel) {
            return 0;
        }

        return Math.min(startValue + (level - startLevel)*incrementPerLevel, maxValue);
    }

}
