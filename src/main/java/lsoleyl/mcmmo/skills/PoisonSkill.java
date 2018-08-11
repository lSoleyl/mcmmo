package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class PoisonSkill implements ISkill {

    public final static LinearProbabilityProgression poisonCancelChance = new LinearProbabilityProgression(0.002);
    public final static LinearProbabilityProgression witherCancelChance = new LinearProbabilityProgression(250, 0.01, 0.0015, 1.0);
    public final static LinearValueProgression witherArrowDuration = new LinearValueProgression(900, 2, 25, 1);


    public final static int XP_PER_DAMAGE = 200;
    public final static int WITHER_MULTIPLIER = 3; // Damage taken by wither rewards more xp
    public final static int EFFECT_CANCEL_COOLDOWN = 5; //s


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("POISON"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by poison or wither"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Blood toxicity", "Chance to cancel an active poison effect."));
        chat.writeMessage(ChatFormat.formatEffect("Blessed blood", "Chance to cancel an active wither effect", witherCancelChance.startLevel));
        chat.writeMessage(ChatFormat.formatEffect("Wither arrows", "Shoot wither arrows", witherArrowDuration.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Blood toxicity chance", ChatFormat.formatPercent(poisonCancelChance.getValue(level))));
        if (level >= witherCancelChance.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Blessed blood chance", ChatFormat.formatPercent(witherCancelChance.getValue(level))));
        }

        if (level >= witherArrowDuration.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Wither arrow effect duration", witherArrowDuration.getValue(level) + "s"));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("POISON"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by poison or wither."));
        chat.writeMessage(" The XP is proportional to the suffered damage. " +
                "Damage caused by wither yields " + WITHER_MULTIPLIER + " times more XP.");
        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Blood toxicity", ""));
        chat.writeMessage(" Your blood becomes gradually more immune to poison. " +
                "The chance to cancel the active poison effect increases by " +
                ChatFormat.formatPercent(poisonCancelChance.incrementPerLevel) +
                " each level. Cancelling a negative effect has a cooldown of " + EFFECT_CANCEL_COOLDOWN + "s.");
        chat.writeMessage(ChatFormat.formatEffect("Blessed blood", "", witherCancelChance.startLevel));
        chat.writeMessage(" Your blood has now even developed a resistance to withering. " +
                "The chance to cancel an active wither effects starts at " + ChatFormat.formatPercent(witherCancelChance.startValue) +
                " at level " + witherCancelChance.startLevel + ". The value increases by " +
                ChatFormat.formatPercent(witherCancelChance.incrementPerLevel) + " each level. Cancelling a negative effect " +
                "has a cooldown of " + EFFECT_CANCEL_COOLDOWN + "s.");

        chat.writeMessage(ChatFormat.formatEffect("Wither arrow", "", witherArrowDuration.startLevel));
        chat.writeMessage(" Shooting arrows will give the target a wither effect. The wither effect will last longer the higher the level."+
                " The wither effect lasts " + witherArrowDuration.startValue + "s at level " + witherArrowDuration.startLevel +
                ". The value increases by " + witherArrowDuration.incrementValue + "s every " + witherArrowDuration.incrementLevelStep + " levels.");
    }
}
