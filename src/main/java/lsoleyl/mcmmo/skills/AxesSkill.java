package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import net.minecraft.entity.player.EntityPlayerMP;

public class AxesSkill implements IAbilitySkill {
    public static final LinearProbabilityProgression criticalChance = new LinearProbabilityProgression(0.0007);
    public static final LinearValueProgression axeMasterDamage = new LinearValueProgression(75, 2, 75, 1);
    public static final LinearValueProgression armorImpactDamage = new LinearValueProgression(100, 1, 100, 1);
    public static final LinearValueProgression skullSplitterDuration = new LinearValueProgression(0, 5, 50, 3);

    public static final int SKULL_SPLITTER_COOLDOWN = 120; //s

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("AXES"));
        chat.writeMessage(ChatFormat.formatXpGain("Attacking enemies with an axe"));

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Skull splitter", "Added AOE damage"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Critical strikes", "Chance to deal double damage"));
        chat.writeMessage(ChatFormat.formatEffect("Axe Mastery", "Additional axe damage", axeMasterDamage.startLevel));
        chat.writeMessage(ChatFormat.formatEffect("Armor Impact", "Directly damage armor", armorImpactDamage.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Critical chance", ChatFormat.formatPercent(criticalChance.getValue(level))));
        if (level >= axeMasterDamage.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Additional axe damage", axeMasterDamage.getValue(level) + ""));
        }
        if (level >= armorImpactDamage.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Additional armor damage", armorImpactDamage.getValue(level) + ""));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("AXES"));
        chat.writeMessage(ChatFormat.formatXpGain("Attacking enemies with an axe."));
        chat.writeMessage(" XP is only given for hurting harmful creatures and players.");

        chat.writeMessage(ChatFormat.formatCaption("ABILITY"));
        chat.writeMessage(ChatFormat.formatEffect("Skull splitter", ""));
        chat.writeMessage(" Sneak right click to prepare ability, attack within 3s to activate the ability."+
                " While active, skull splitter will deal 50% of the attack damage to every other entity within a radius of" +
                " one block around the target. The duration starts at " + skullSplitterDuration.startValue + "s and increases by " + skullSplitterDuration.incrementValue + "s every " +
                skullSplitterDuration.incrementLevelStep + " levels. The cooldown is " + SKULL_SPLITTER_COOLDOWN + "s.");

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Critical strikes", ""));
        chat.writeMessage(" You get a chance to twice the damage. The chance increases by " + ChatFormat.formatPercent(criticalChance.incrementPerLevel) +
                " per level up to " + ChatFormat.formatPercent(criticalChance.maxValue));

        chat.writeMessage(ChatFormat.formatEffect("Axe Mastery", "", axeMasterDamage.startLevel));
        chat.writeMessage(" Increases the base axe damage by " + axeMasterDamage.startValue + " at level " + axeMasterDamage.startLevel +
                " and by " + axeMasterDamage.incrementLevelStep + " every " + axeMasterDamage.incrementLevelStep + " level.");

        chat.writeMessage(ChatFormat.formatEffect("Armor Impact", "", armorImpactDamage.startLevel));
        chat.writeMessage(" The blunt force of the axe puts extra strain on the enemy armor and destroys it faster than anything else."+
            " The axe deals " + armorImpactDamage.startValue + " additional armoe damage at level " + armorImpactDamage.startLevel +
            " and increases by " + armorImpactDamage.incrementValue + " every " + armorImpactDamage.incrementLevelStep + " levels.");
    }


    @Override
    public void onPrepare(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("You ready your axe..."));
    }

    @Override
    public void onPrepareTimeout(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("You lower your axe..."));
    }

    @Override
    public void onAbilityActivate(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("SPLITTING SKULLS!!!"));
    }

    @Override
    public void onAbilityWearOut(EntityPlayerMP player) {
        ChatWriter.writeMessage(player, ChatFormat.formatAbilityMessage("Skull splitter has worn out"));
    }

}
