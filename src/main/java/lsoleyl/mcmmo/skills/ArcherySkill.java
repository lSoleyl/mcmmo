package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class ArcherySkill implements ISkill {
    public static final LinearProbabilityProgression skillShotDamage = new LinearProbabilityProgression(0.003, 10);
    public static final LinearProbabilityProgression catchChance = new LinearProbabilityProgression(0.001);
    public static final LinearProbabilityProgression curveShotChance = new LinearProbabilityProgression(900, 0, 0.002, 0.2);

    public static final int CATCH_XP = 500; // xp per caught arrow
    public static final double CURVE_SHOT_DAMAGE_FACTOR = 2.0;

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("ARCHERY"));
        chat.writeMessage(ChatFormat.formatXpGain("Shooting enemies with arrows."));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Skill shot", "Increased damage"));
        chat.writeMessage(ChatFormat.formatEffect("Arrow catch", "Chance to catch incoming arrows"));
        chat.writeMessage(ChatFormat.formatEffect("Curve shot", "Deal extra damage", curveShotChance.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Additional damage", ChatFormat.formatPercent(skillShotDamage.getValue(level))));
        chat.writeMessage(ChatFormat.formatEffectValue("Catch chance", ChatFormat.formatPercent(catchChance.getValue(level))));
        if (level >= curveShotChance.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Curve shot chance", ChatFormat.formatPercent(curveShotChance.getValue(level))));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("ARCHERY"));
        chat.writeMessage(ChatFormat.formatXpGain("Shooting enemies with arrows."));
        chat.writeMessage(" Attacking peaceful mobs won't yield any xp.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Skill shot", ""));
        chat.writeMessage(" Your arrows deal increasingly more damage. The damage increases by " +
                ChatFormat.formatPercent(skillShotDamage.incrementPerLevel) + " every level.");

        chat.writeMessage(ChatFormat.formatEffect("Arrow catch", ""));
        chat.writeMessage(" As you get more skilled with the bow, you learn to read the arrows' movements and how to catch them." +
                " The chance to catch an incoming arrow increases by " + ChatFormat.formatPercent(catchChance.incrementPerLevel) + " every level.");

        chat.writeMessage(ChatFormat.formatEffect("Curve shot", "", curveShotChance.startLevel));
        chat.writeMessage(" Shooting the arrow in a curvy path, makes them impossible to catch and more easily " +
            " hits vital organs and deals " + ChatFormat.formatPercent(CURVE_SHOT_DAMAGE_FACTOR) + " damage on top of the skill shot effect."+
            " The curve shot chance increases by " + ChatFormat.formatPercent(curveShotChance.incrementPerLevel) + " per level up to " +
            ChatFormat.formatPercent(curveShotChance.maxValue) + ". A skill shot which would have otherwise been caught doesn't " +
            " cause the increased damage.");
    }
}
