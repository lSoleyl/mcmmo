package lsoleyl.mcmmo.utility;

import java.util.Random;

public class Rand {
    private static final Random random = new Random();

    public static boolean evaluate(double chance) {
        return random.nextDouble() < chance;
    }
}
