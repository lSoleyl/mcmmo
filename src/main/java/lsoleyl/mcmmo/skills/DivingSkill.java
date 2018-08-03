package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class DivingSkill implements ISkill {
    public static final LinearProbabilityProgression AIR_RESTORE_CHANCE = new LinearProbabilityProgression(0.001);
    public static final int XP_PER_DAMAGE = 250;


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("DIVING"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by drowning"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Last breath", "Chance to refill your lungs on drowning"));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Last breath chance", ChatFormat.formatPercent(AIR_RESTORE_CHANCE.getValue(level))));
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("DIVING"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by drowning."));
        chat.writeMessage(" The XP is proportional to the suffered damage.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Last breath", ""));
        chat.writeMessage(" Long diving training pays off. You get a chance to refill your lungs with air upon taking damage from drowning." +
                " The chance to refill your lungs increases by " + ChatFormat.formatPercent(AIR_RESTORE_CHANCE.incrementPerLevel) +
                " per level.");
    }
}
