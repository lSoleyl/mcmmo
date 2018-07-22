package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.utility.ChatWriter;

/** The base interface for all skills. Skill shouldn't be instantiated, they should rather be
 *  referenced by the skill registry, which should hold a reference to each available skill.
 */
public interface ISkill {
    /** Prints the skill's description to the chat log of the given player.
     *  The skills level is passed to print the current effects of this skill.
     *
     * @param chat the chat to write the info into
     * @param level the player's current level for that skill
     */
    void printDescription(ChatWriter chat, int level);

    /** Prints a generic skill description to the given chat
     */
    void printHelp(ChatWriter chat);
}
