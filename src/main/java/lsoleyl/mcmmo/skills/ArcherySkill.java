package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class ArcherySkill implements ISkill {
    public static final LinearProbabilityProgression skillShotDamage = new LinearProbabilityProgression(0.003, 10);
    //TODO add arrow catch chance
    //TODO add arrow drop chance

    public static final int XP_PER_DAMAGE = 50; // same value as unarmed should be ok

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("ARCHERY"));
        chat.writeMessage(ChatFormat.formatXpGain("Shooting enemies with arrows."));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Skill shot", ""));
        chat.writeMessage(" Your arrows deal increasingly more damage. The damage increases by " +
                ChatFormat.formatPercent(skillShotDamage.incrementPerLevel) + " every level.");

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Additional damage", ChatFormat.formatPercent(skillShotDamage.getValue(level))));
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("ARCHERY"));
        chat.writeMessage(ChatFormat.formatXpGain("Shooting enemies with arrows."));
        chat.writeMessage(" Attacking peaceful mobs won't yield any xp.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Skill shot", ""));
        chat.writeMessage(" Your arrows deal increasingly more damage. The damage increases by " +
                ChatFormat.formatPercent(skillShotDamage.incrementPerLevel));
    }
}
