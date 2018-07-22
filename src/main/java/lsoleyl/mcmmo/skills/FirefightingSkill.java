package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.skills.progression.LinearProbabilityProgression;
import lsoleyl.mcmmo.skills.progression.LinearValueProgression;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

/** A new skill, which reduces damage taken by fire, lava and explosions
 *  It also adds explosion resistance at level 200
 */
public class FirefightingSkill implements ISkill {
    public static final LinearProbabilityProgression fireResistanceChance = new LinearProbabilityProgression(0.002); //100% at lvl 500
    public static final LinearValueProgression explosionDamageReduction = new LinearValueProgression(200, 2, 50, 1); // Will be 16dmg reduction at lvl 1000
    public static int XP_PER_DAMAGE = 250;
    public static int EXPLOSION_XP_MULTIPLIER = 3; // triple xp for suffering explosion damage


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("FIREFIGHTING"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by fire, lava or explosions"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Fireproof", "Chance to not take damage when on fire"));
        chat.writeMessage(ChatFormat.formatEffect("Tough skin", "Reduced damage from explosions", explosionDamageReduction.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Fireproof chance", ChatFormat.formatPercent(fireResistanceChance.getValue(level))));
        if (level >= explosionDamageReduction.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Explosion damage reduction", explosionDamageReduction.getValue(level) + ""));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        //TODO write help text for this skill
    }
}
