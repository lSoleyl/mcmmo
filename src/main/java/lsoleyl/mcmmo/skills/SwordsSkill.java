package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import net.minecraft.entity.player.EntityPlayerMP;

public class SwordsSkill implements IAbilitySkill {
    public static final LinearProbabilityProgression poisonChance = new LinearProbabilityProgression(0.001, 0.75);
    public static final LinearValueProgression poisonDuration = new LinearValueProgression(0,1,50,1);
    public static final LinearValueProgression poisonPotency = new LinearValueProgression(500, 1, 500 , 1, 2);

    public static final LinearValueProgression cleanCutterDamageMultiplier = new LinearValueProgression(0, 2, 250, 1);
    public static final int CLEAN_CUTTER_DURATION = 30; //s this is not levelling, this is static
    public static final int CLEAN_CUTTER_COOLDOWN = 120;


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("SWORDS"));
        chat.writeMessage(ChatFormat.formatXpGain("Attacking enemies with a sword"));

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Clean cutter", "Multiplied damage for " + CLEAN_CUTTER_DURATION + "s"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Poisoned blade", "Chance to poison your opponent"));
        chat.writeMessage(ChatFormat.formatEffect("Nasty posion", "Increased poison effect duration"));
        chat.writeMessage(ChatFormat.formatEffect("Poison potency", "Increases the potency of the applied poison", poisonPotency.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Clean cutter damage multiplier", "x"+cleanCutterDamageMultiplier.getValue(level)));
        chat.writeMessage(ChatFormat.formatEffectValue("Poisoned chance", ChatFormat.formatPercent(poisonChance.getValue(level))));
        chat.writeMessage(ChatFormat.formatEffectValue("Poison duration", poisonDuration.getValue(level)+"s"));
        if (level >= poisonPotency.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Posion potency level", (poisonPotency.getValue(level)+1)+""));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("SWORDS"));
        chat.writeMessage(ChatFormat.formatXpGain("Attacking enemies with a sword"));
        chat.writeMessage(" XP is only given for hurting harmful creatures and players.");

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Clean cutter", ""));
        chat.writeMessage(" Sneak right click to prepare ability, attack within 3s to activate the ability."+
                " While active, clean cutter will multiply the dealt damage for " + CLEAN_CUTTER_DURATION + "s." +
                " The damage multiplier starts at " + cleanCutterDamageMultiplier.startValue + " and increases by " + cleanCutterDamageMultiplier.incrementValue+ " every " +
                cleanCutterDamageMultiplier.incrementLevelStep + " levels. The cooldown is " + CLEAN_CUTTER_COOLDOWN + "s.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Poisoned blade", ""));
        chat.writeMessage(" Coating your blade in poison gives you a chance to poison your opponent. The chance increases by "+
            ChatFormat.formatPercent(poisonChance.incrementPerLevel) + " each level up to " + ChatFormat.formatPercent(poisonChance.maxValue));

        chat.writeMessage(ChatFormat.formatEffect("Nasty poison", ""));
        chat.writeMessage(" You slowly learn how to extend the effective duration of your poison." +
            " The duration starts at " + poisonDuration.startValue + "s and increases by " + poisonDuration.incrementValue + "s every "+
            poisonDuration.incrementLevelStep + " levels.");
        chat.writeMessage(ChatFormat.formatEffect("Poison potency", "", poisonPotency.startLevel));
        chat.writeMessage(" You now know how to mix a poison, which strikes down your opponents even faster. The poisons potency "+
            " Increases by one level at level " + poisonPotency.startLevel + " and by another level at level " + (poisonPotency.startLevel + poisonPotency.incrementLevelStep));
    }

    @Override
    public void onPrepare(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("You ready your sword..."));
    }

    @Override
    public void onPrepareTimeout(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("You lower your sword..."));
    }

    @Override
    public void onAbilityActivate(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("CUTTING THROUGH THEIR LINES!!!"));
    }

    @Override
    public void onAbilityWearOut(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("Clean cutter has worn out"));
    }
}
