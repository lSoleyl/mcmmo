package lsoleyl.mcmmo.skills.progression;

public class LinearValueProgression {
    public final int startLevel;
    public final int startValue;
    public final int incrementLevelStep; // after how many levels the value gets incremented each time
    public final int incrementValue;     // by how much to increment every step levels
    public final int maxValue;


    public LinearValueProgression(int startLevel, int startValue, int incrementLevelStep, int incrementValue, int maxValue) {
        this.startLevel = startLevel;
        this.startValue = startValue;
        this.incrementLevelStep = incrementLevelStep;
        this.incrementValue = incrementValue;
        this.maxValue = maxValue;
    }

    public LinearValueProgression(int startLevel, int startValue, int incrementLevelStep, int incrementValue) {
        this(startLevel, startValue, incrementLevelStep, incrementValue, Integer.MAX_VALUE);
    }

    public int getValue(int level) {
        if (level < startLevel) {
            return 0;
        }

        return Math.min(startValue + ((level - startLevel)/incrementLevelStep)*incrementValue, maxValue);
    }
}
