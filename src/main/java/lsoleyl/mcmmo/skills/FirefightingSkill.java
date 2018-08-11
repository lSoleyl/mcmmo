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
    public static final LinearValueProgression fireArrowFireDuration = new LinearValueProgression(800, 2, 25, 1);
    public static int XP_PER_DAMAGE = 250;
    public static int EXPLOSION_XP_MULTIPLIER = 3; // triple xp for suffering explosion damage


    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("FIREFIGHTING"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by fire, lava or explosions"));

        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Fireproof", "Chance to not take damage when on fire"));
        chat.writeMessage(ChatFormat.formatEffect("Tough skin", "Reduced damage from explosions", explosionDamageReduction.startLevel));
        chat.writeMessage(ChatFormat.formatEffect("Fireworks", "Shoot fire arrows", fireArrowFireDuration.startLevel));

        chat.writeMessage(ChatFormat.formatCaption("YOUR STATS"));
        chat.writeMessage(ChatFormat.formatEffectValue("Fireproof chance", ChatFormat.formatPercent(fireResistanceChance.getValue(level))));
        if (level >= explosionDamageReduction.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Explosion damage reduction", explosionDamageReduction.getValue(level) + ""));
        }

        if (level >= fireArrowFireDuration.startLevel) {
            chat.writeMessage(ChatFormat.formatEffectValue("Fireworks fire duration", fireArrowFireDuration.getValue(level) + "s"));
        }
    }

    @Override
    public void printHelp(ChatWriter chat) {
        chat.writeMessage(ChatFormat.formatCaption("FIREFIGHTING"));
        chat.writeMessage(ChatFormat.formatXpGain("Taking damage by fire, lava or explosions."));
        chat.writeMessage(" The XP is proportional to the suffered damage. " +
                "Damage caused by explosions yields " + EXPLOSION_XP_MULTIPLIER + " times more XP.");
        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
        chat.writeMessage(ChatFormat.formatEffect("Fireproof", ""));
        chat.writeMessage(" Chance to not take damage when on fire. " +
                "This can also negate the damage taken by lava. The probability to not take damage increases by " +
                 ChatFormat.formatPercent(fireResistanceChance.incrementPerLevel) +
                " each level.");
        chat.writeMessage(ChatFormat.formatEffect("Tough skin", "", explosionDamageReduction.startLevel));
        chat.writeMessage(" Suffered explosion damage is reduced. " +
                "Damage is reduced by " + explosionDamageReduction.startValue + " at level " + explosionDamageReduction.startLevel +
                ". The value increases by " + explosionDamageReduction.incrementValue + " every " + explosionDamageReduction.incrementLevelStep + " levels.");

        chat.writeMessage(ChatFormat.formatEffect("Fireworks", "", fireArrowFireDuration.startLevel));
        chat.writeMessage(" Shooting arrows will give the target a fire effect. The fire effect will last longer the higher the level."+
                " The fire effect lasts " + fireArrowFireDuration.startValue + "s at level " + fireArrowFireDuration.startLevel +
                ". The value increases by " + fireArrowFireDuration.incrementValue + "s every " + fireArrowFireDuration.incrementLevelStep + " levels.");
    }
}
