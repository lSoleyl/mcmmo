package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class ParkourSkill implements ISkill {
    public final static LinearProbabilityProgression PARKOUR_ROLL_DAMAGE_REDUCTION = new LinearProbabilityProgression(0,0.1,0.0016,0.5);
    public final static LinearProbabilityProgression PERFECT_ROLL_CHANCE = new LinearProbabilityProgression(0.001);

    public final static int XP_PER_DAMAGE = 250;
    public final static int ROLL_XP = 500;
    public final static int PERFECT_ROLL_XP = 1500;

    public final static int SPEED_BOOST_DURATION = 5; //s


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("PARKOUR"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by falling or performing rolls"));

        chat.writeMessage(ChatFormat.formatCaption("ABILITIES"));
        chat.writeMessage(ChatFormat.formatEffect("Parkour roll", "Reduces fall damage"));
        chat.writeMessage(ChatFormat.formatEffect("Perfect roll", "Chance to completely negate fall damage"));


        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Parkour roll damage reduction", ChatFormat.formatPercent(PARKOUR_ROLL_DAMAGE_REDUCTION.getValue(level))));
        chat.writeMessage(ChatFormat.formatEffectValue("Perfect roll chance", ChatFormat.formatPercent(PERFECT_ROLL_CHANCE.getValue(level))));
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("PARKOUR"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by falling or performing rolls."));

        chat.writeMessage(ChatFormat.formatCaption("ABILITIES"));
        chat.writeMessage(ChatFormat.formatEffect("Parkour roll", ""));
        chat.writeMessage(" Hold sneak while landing to perform a roll to reduce the damage. The roll damage reduction starts" +
            " at " + ChatFormat.formatPercent(PARKOUR_ROLL_DAMAGE_REDUCTION.startValue) + " and increases by " +
            ChatFormat.formatPercent(PARKOUR_ROLL_DAMAGE_REDUCTION.incrementPerLevel) + " per level up to " +
            ChatFormat.formatPercent(PARKOUR_ROLL_DAMAGE_REDUCTION.maxValue) + ".");

        chat.writeMessage(ChatFormat.formatEffect("Perfect roll", ""));
        chat.writeMessage(" While performing a roll you get a chance to perform a perfect roll, which completely negates all" +
            " fall damage. The chance to perform a perfect roll increases by " + ChatFormat.formatPercent(PERFECT_ROLL_CHANCE.incrementPerLevel) +
            " per level. Peforming a perfect roll will also give you a speed boost for " + SPEED_BOOST_DURATION + " seconds.");
    }
}
