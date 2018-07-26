package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import net.minecraft.entity.player.EntityPlayerMP;

public class UnarmedSkill implements IAbilitySkill {
    public static final LinearValueProgression ironArmDamage = new LinearValueProgression(0, 0, 50, 1);
    public static final LinearProbabilityProgression disarmChance = new LinearProbabilityProgression(0.0005, 0.33);
    public static final LinearProbabilityProgression ironGripChance = new LinearProbabilityProgression(0.001);
    public static final LinearValueProgression berserkDuration = new LinearValueProgression(0, 5, 50, 5);

    public static final double BERSERK_DAMAGE_MULTIPLIER = 1.5;
    public static final int BERSERK_COOLDOWN = 120;


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("UNARMED"));
        chat.writeMessage(ChatFormat.formatXpGain("Dealing damage while unarmed"));

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Berserk", "50% increased unhanded damage"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Iron Arm", "Increased unarmed damage"));
        chat.writeMessage(ChatFormat.formatEffect("Disarm", "Chance to disarm your opponent"));
        chat.writeMessage(ChatFormat.formatEffect("Iron Grip", "Protection against disarm"));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Berserk duration", berserkDuration.getValue(level) + "s"));
        chat.writeMessage(ChatFormat.formatEffectValue("Iron Arm extra damage", ironArmDamage.getValue(level)+""));
        chat.writeMessage(ChatFormat.formatEffectValue("Disarm chance", ChatFormat.formatPercent(disarmChance.getValue(level))));
        chat.writeMessage(ChatFormat.formatEffectValue("Iron grip chance", ChatFormat.formatPercent(ironGripChance.getValue(level))));

    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("UNARMED"));
        chat.writeMessage(ChatFormat.formatXpGain("Dealing damage while unarmed."));
        chat.writeMessage(" XP is only given for hurting harmful creatures and players.");

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Berserk", ""));
        chat.writeMessage(" Shift right click to prepare ability, attack within 3s to activate the ability."+
            " While active, Berserk will increase unarmed damage by 50%. The abilities duration starts at " +
            berserkDuration.startValue + "s and increases by " + berserkDuration.incrementValue + "s every " +
            berserkDuration.incrementLevelStep + " levels. The cooldown is " + BERSERK_COOLDOWN + "s.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Iron Arm", ""));
        chat.writeMessage(" Every punch deals an increased amount of damage."+
            " The damage increases by " + ironArmDamage.incrementValue + " every " + ironArmDamage.incrementLevelStep + " levels.");

        chat.writeMessage(ChatFormat.formatEffect("Disarm", ""));
        chat.writeMessage(" While attacking you have a chance to disarm your opponent so that"+
            " he drops his equipped item to the ground. Only works against players."+
            " The chance to disarm increases by " + ChatFormat.formatPercent(disarmChance.incrementPerLevel) +
            " per level to a maximum chance of " + ChatFormat.formatPercent(disarmChance.maxValue));

        chat.writeMessage(ChatFormat.formatEffect("Iron Grip", ""));
        chat.writeMessage(" Gives you a chance to prevent yourself from getting disarmed by another player." +
            " The chance increases by " + ChatFormat.formatPercent(ironGripChance.incrementPerLevel) + " per level to a " +
            " maximum of " + ChatFormat.formatPercent(ironGripChance.maxValue) + ".");
    }

    @Override
    public void onPrepare(EntityPlayerMP player) {
        new ChatWriter(player).writeMessage(ChatFormat.formatAbilityMessage("You ready your fists..."));
    }

    @Override
    public void onPrepareTimeout(EntityPlayerMP player) {
        new ChatWriter(player).writeMessage(ChatFormat.formatAbilityMessage("You lower your fists"));
    }

    @Override
    public void onAbilityActivate(EntityPlayerMP player) {
        new ChatWriter(player).writeMessage(ChatFormat.formatAbilityMessage("BERSERK!!!"));
    }

    @Override
    public void onAbilityWearOut(EntityPlayerMP player) {
        new ChatWriter(player).writeMessage(ChatFormat.formatAbilityMessage("Berserk has worn out"));
    }
}
