package lsoleyl.mcmmo.utility;

import joptsimple.internal.Strings;
import lsoleyl.mcmmo.skills.Skill;
import net.minecraft.util.EnumChatFormatting;

import java.text.DecimalFormat;

/** This is a utility class for formatting various messages in a unified way
 *
 */
public class ChatFormat {
    public static String formatLevel(int level) {
        return EnumChatFormatting.GREEN.toString() + level;
    }

    public static String formatPowerLevel(int powerLevel) {
        return EnumChatFormatting.DARK_RED + "POWER_LEVEL: " + formatLevel(powerLevel);
    }

    public static String formatLevelXp(long current, long total) {
        return EnumChatFormatting.DARK_AQUA + "XP(" + current + "/" + total + ")";
    }

    public static String formatSkill(Skill skill) {
        return EnumChatFormatting.YELLOW + skill.toString();
    }

    /** Formats the given caption in the way mcmmo messages are formatted
     */
    public static String formatCaption(String caption) {
        final int fieldWidth = 20; // Determines the number of dashes, which are printed around the caption

        // determine the number of dashes to add around the text
        int dashes = (fieldWidth - caption.length() - 4) / 2;
        if (dashes < 1) {
            // No matter how long the text is, always surround it with -[]...[]-
            dashes = 1;
        }

        String dashString = Strings.repeat('-', dashes);

        return EnumChatFormatting.RED + dashString + "[]" + EnumChatFormatting.GREEN + caption + EnumChatFormatting.RED + "[]" + dashString;
    }

    public static String formatXpGain(String xpSource) {
        return EnumChatFormatting.DARK_GRAY + "XP GAIN: " + EnumChatFormatting.WHITE + xpSource;
    }

    public static String formatEffect(String name, String description) {
        return EnumChatFormatting.DARK_AQUA + name + ": " + EnumChatFormatting.GREEN + description;
    }

    public static String formatEffect(String name, String description, int startLevel) {
        return EnumChatFormatting.DARK_AQUA + name +
                    " [" + EnumChatFormatting.YELLOW + startLevel + "+" + EnumChatFormatting.DARK_AQUA + "]: " +
                    EnumChatFormatting.GREEN + description;
    }

    public static String formatEffectValue(String name, String value) {
        return EnumChatFormatting.RED + name + ": " + EnumChatFormatting.YELLOW + value;
    }

    public static String formatPercent(double percent) {
        return new DecimalFormat("0.00").format(percent * 100) + "%";
    }

    public static String formatLevelUp(Skill skill, int level) {
        return EnumChatFormatting.YELLOW + "Your " + skill + " has reached level " + EnumChatFormatting.GREEN + level;
    }

    public static String formatEffectActivated(String message) {
        return EnumChatFormatting.GRAY + message;
    }

    public static String formatAbilityMessage(String message) { return EnumChatFormatting.DARK_GREEN + message; }

    public static String formatCooldown(String abilityName, int cooldown) {
        return EnumChatFormatting.GRAY + abilityName + " is still on cooldown (" + EnumChatFormatting.YELLOW + cooldown + EnumChatFormatting.GRAY + "s)";
    }

    public static String formatCommand(String command) {
        return EnumChatFormatting.DARK_AQUA + command;
    }

    public static String formatRank(int position, String player, int level) {
        return EnumChatFormatting.YELLOW + "" + position + ". " + player + ": " + EnumChatFormatting.GREEN + level;
    }

    private ChatFormat() {}
}
