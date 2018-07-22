package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class MiningSkill implements ISkill {
    public static final LinearProbabilityProgression doubleDropChance = new LinearProbabilityProgression(0.001);
    public static final LinearValueProgression superBreakerDuration = new LinearValueProgression(0, 2, 50, 1);

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("MINING"));
        chat.writeMessage(ChatFormat.formatXpGain("Mining Stone & Ore"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Super Breaker (ABILITY)", "Speed+, Triple Drop Chance"));
        chat.writeMessage(ChatFormat.formatEffect("Double Drops", "Double the normal loot"));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Double Drop Chance", ChatFormat.formatPercent(doubleDropChance.getValue(level))));
        chat.writeMessage(ChatFormat.formatEffectValue("Super Breaker Length", superBreakerDuration.getValue(level) + "s"));
    }

    @Override
    public void printHelp(ChatWriter chat) {
        //TODO write some help
    }
}
