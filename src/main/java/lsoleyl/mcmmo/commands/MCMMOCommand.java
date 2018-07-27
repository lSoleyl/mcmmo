package lsoleyl.mcmmo.commands;

import com.google.common.collect.ImmutableList;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.skills.SkillRegistry;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import lsoleyl.mcmmo.utility.Tuple;
import lsoleyl.mcmmo.utility.Optional;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;


public class MCMMOCommand implements ICommand {


    @Override
    public String getCommandName() {
        return "mcmmo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mcmmo <command> - for more info run /mcmmo help";
    }

    @Override
    public List getCommandAliases() {
        return new LinkedList<String>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            System.out.println("Cannot run mcmmo command from server console");
            return;
        }

        // Immutable lists are better suited for slicing
        ImmutableList<String> arguments = ImmutableList.copyOf(args);

        EntityPlayerMP player = (EntityPlayerMP)sender;
        ChatWriter chat = new ChatWriter(player);

        if (arguments.size() == 0) {
            // only /mcmmo has been run -> treat it like help
            helpCommand(player, arguments);
            return;
        }


        String command = arguments.get(0);
        ImmutableList<String> tail = arguments.subList(1, arguments.size());
        if (command.equals("help")) {
            helpCommand(player, tail);
        } else if (command.equals("skills")) {
            skillsCommand(player);
        } else if (command.equals("stats")) {
            statsCommand(chat, tail);
        } else if (command.equals("inspect")) {
            inspectCommand(chat, tail);
        } else {
            Optional<Skill> skill = Skill.getByName(arguments.get(0));
            if (skill.isPresent()) {
                skillCommand(player, skill.get());
                return;
            }

            chat.writeMessage("Unknown /mcmmo command: " + arguments.get(0));
        }
    }

    private void inspectCommand(ChatWriter chat, List<String> arguments) {
        if (arguments.isEmpty()) {
            chat.writeMessage("Missing player parameter in /mcmmo inspect command");
        } else {
            String playerName = arguments.get(0);
            Optional<PlayerXp> xp = MCMMO.getPlayerXp(playerName);

            if (!xp.isPresent()) {
                chat.writeMessage("No player " + playerName + " found in MCMMO registry");
            } else {
                chat.writeMessage(ChatFormat.formatCaption(playerName));
                printSkills(chat, xp.get());
            }
        }
    }


    private List<Tuple<String, Integer>> getSortedPlayerLevelList(ISkillLevelMapper mapper) {
        ArrayList<Tuple<String, Integer>> list = new ArrayList<Tuple<String, Integer>>();
        for (String name : MCMMO.getPlayerNames()) {
            list.add(new Tuple<String,Integer>(name, mapper.getLevel(MCMMO.getPlayerXp(name).get())));
        }
        Collections.sort(list, new Comparator<Tuple<String, Integer>>() {
            @Override
            public int compare(Tuple<String, Integer> o1, Tuple<String, Integer> o2) {
                return o2.b.compareTo(o1.b);
            }
        });

        return list;
    }


    private void statsCommand(ChatWriter chat, List<String> arguments) {
        if (arguments.isEmpty()) {
            // rank by powerlevel
            List<Tuple<String, Integer>> sortedList = getSortedPlayerLevelList(new ISkillLevelMapper() {
                @Override
                public int getLevel(PlayerXp xp) {
                    return xp.getPowerLevel();
                }
            });

            int position = 1;
            chat.writeMessage(ChatFormat.formatCaption("POWERLEVEL"));
            for (Tuple<String, Integer> entry : sortedList) {
                chat.writeMessage(ChatFormat.formatRank(position, entry.a, entry.b));
                ++position;
            }
        } else {
            final Optional<Skill> skill = Skill.getByName(arguments.get(0));
            if (!skill.isPresent()) {
                // Invalid skill passed
                chat.writeMessage("No skill named " + arguments.get(0) + " found!");
            } else {
                // Now list all players by the skill's level in descending order
                List<Tuple<String, Integer>> sortedList = getSortedPlayerLevelList(new ISkillLevelMapper() {
                    @Override
                    public int getLevel(PlayerXp xp) {
                        return xp.getSkillXp(skill.get()).getLevel();
                    }
                });

                int position = 1;
                chat.writeMessage(ChatFormat.formatCaption(skill.get().name()));
                for (Tuple<String, Integer> entry : sortedList) {
                    chat.writeMessage(ChatFormat.formatRank(position, entry.a, entry.b));
                    ++position;
                }
            }
        }

    }

    private void helpCommand(EntityPlayerMP player, List<String> arguments) {
        ChatWriter chat = new ChatWriter(player);

        if (arguments.isEmpty()) {
            chat.writeMessage(ChatFormat.formatCaption("MCMMO - Forge"));
            chat.writeMessage("This mod adds rpg elements to minecraft by adding combat and utility skills,"+
                " which are leveled by using them. To get a list of all available skills and your current level, use following command:");
            chat.writeMessage(ChatFormat.formatCommand("/mcmmo skills"));
            chat.writeMessage("To see the effects of a single skill, just run:");
            chat.writeMessage(ChatFormat.formatCommand("/mcmmo <skillname>"));
            chat.writeMessage("To see a detailed explanation of that skill, run:");
            chat.writeMessage(ChatFormat.formatCommand("/mcmmo help <skillname>"));
            chat.writeMessage("To see how your skill level compares to other players, run:");
            chat.writeMessage(ChatFormat.formatCommand("/mcmmo stats"));
            chat.writeMessage("You can also append the skill name to only compare the levels of that skill.");
            chat.writeMessage("To see all skill levels of a specific player, run:");
            chat.writeMessage(ChatFormat.formatCommand("/mcmmo inspect <playername>"));

        } else {
            String topic = arguments.get(0);

            Optional<Skill> skill = Skill.getByName(topic);
            if (skill.isPresent()) {
                SkillRegistry.getInstance().getSkill(skill.get()).printHelp(chat);
                return;
            }

            chat.writeMessage("No help page for " + topic + " found.");
        }
    }

    /** Lists info for a specific skill regarding the player
     *  /mcmmo <skill>
     */
    private void skillCommand(EntityPlayerMP player, Skill skill) {
        XPWrapper xp = MCMMO.getPlayerXp(player).getSkillXp(skill);
        ChatWriter chat = new ChatWriter(player);
        SkillRegistry.getInstance().getSkill(skill).printDescription(chat, xp.getLevel());
        chat.writeMessage(""); // add empty line
        chat.writeMessage(EnumChatFormatting.YELLOW + "Level " + xp);
    }

    /** Lists the current level of each skill and the total power level
     *  /mcmmo skills
     */
    private void skillsCommand(EntityPlayerMP player) {
        printSkills(new ChatWriter(player), MCMMO.getPlayerXp(player));
    }

    private void printSkills(ChatWriter chat, PlayerXp xp) {
        int powerLevel = 0;

        for(Skill skill : Skill.values()) {
            XPWrapper skillXp = xp.getSkillXp(skill);
            chat.writeMessage(ChatFormat.formatSkill(skill) + " Skill: " + skillXp);
            powerLevel += skillXp.getLevel();
        }

        chat.writeMessage(ChatFormat.formatPowerLevel(powerLevel));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // allow everyone to use this command
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] parts) {
        List<String> completions = new LinkedList<String>();

        if (parts.length <= 1) {
            completions.add("help");
            completions.add("inspect");
            completions.add("skills");
            completions.add("stats");

            for(Skill skill : Skill.values()) {
                completions.add(skill.name().toLowerCase());
            }

            if (parts.length == 1) {
                ListIterator<String> iter = completions.listIterator();
                while(iter.hasNext()) {
                    String completion = iter.next();
                    if (!completion.startsWith(parts[0])) {
                        iter.remove();
                    }
                }
            }
        } else if (parts.length == 2) {
            if (parts[0].equals("help") || parts[0].equals("stats")) {
                // can be followed by a skill name
                for(Skill skill : Skill.values()) {
                    String skillName = skill.name().toLowerCase();
                    if (skillName.startsWith(parts[1])) {
                        completions.add(skillName);
                    }
                }
            } else if(parts[0].equals("inspect")) {
                // inspect can only be followed by a player name
                for(String name : MCMMO.getPlayerNames()) {
                    if (name.startsWith(parts[1])) {
                        completions.add(name);
                    }
                }
            }
        }

        return completions;
    }

    @Override
    public boolean isUsernameIndex(String[] parts, int index) {
        //TODO what is this even used for? It doesn't seem to work
        // /mcmmmo inspect <player>
        if(parts.length >= 1 && parts[0].equals("inspect") && index == 1) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof  ICommand) {
            return getCommandName().compareTo(((ICommand) o).getCommandName());
        } else {
            return -1;
        }
    }
}

/** Interface required for backward compatibility retrofit the lambdas into java6
 */
interface ISkillLevelMapper {
    int getLevel(PlayerXp xp);
}
