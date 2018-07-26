package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class CombatSkill implements ISkill {
    public final static LinearProbabilityProgression dodgeChance = new LinearProbabilityProgression(0.001);
    public final static LinearValueProgression damageReduction = new LinearValueProgression(200,2,50,1);

    public static final int XP_PER_DAMAGE = 100; // this value must be way smaller than the firefighter xp per burn as taking combat damage is way more common
    public static final int DODGE_XP = 500;      // how much xp awarded for a dodge
    public static final int DODGE_COOLDOWN = 3;  // dodge cooldown in seconds

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("COMBAT"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage in close combat"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Dodge", "Chance to take no damage"));
        chat.writeMessage(ChatFormat.formatEffect("Nimble fighter", "Reduce incoming damage", damageReduction.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Dodge chance", ChatFormat.formatPercent(dodgeChance.getValue(level))));
        if (level >= damageReduction.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Damage reduction", damageReduction.getValue(level)+""));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("COMBAT"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage in close combat"));
        chat.writeMessage(" A successful dodge also yields xp.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Dodge", ""));
        chat.writeMessage(" You have a chance of completely dodging a close combat attack and knocking back attacking mobs. "+
            "The dodge chance increases by " + ChatFormat.formatPercent(dodgeChance.incrementPerLevel) + " per level up to " +
            ChatFormat.formatPercent(dodgeChance.maxValue) + ". The dodge has a cooldown of " + DODGE_COOLDOWN + "s");

        chat.writeMessage(ChatFormat.formatEffect("Nimble fighter", "", damageReduction.startLevel));
        chat.writeMessage(" Incoming close combat damage is reduced by a constant value before applying damage to your armor."+
                " The damage is reduced by " + damageReduction.startValue + " at level " + damageReduction.startLevel+"."+
                " The value increases by " + damageReduction.incrementValue + " every " + damageReduction.incrementLevelStep + " levels.");
    }
}
