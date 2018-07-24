package lsoleyl.mcmmo.commands;

import com.google.common.collect.ImmutableList;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.skills.SkillRegistry;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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


        switch(arguments.get(0)) {
            case "help": helpCommand(player, arguments.subList(1, arguments.size())); break;
            case "skills": skillsCommand(player); break;

            //TODO implement remaining commands, including skill commands

            default:
                Optional<Skill> skill = Skill.getByName(arguments.get(0));
                if (skill.isPresent()) {
                    skillCommand(player, skill.get());
                    return;
                }



                chat.writeMessage("Unknown /mcmmo command: " + arguments.get(0));
        }
    }

    private void helpCommand(EntityPlayerMP player, ImmutableList<String> arguments) {
        ChatWriter chat = new ChatWriter(player);

        if (arguments.size() == 0) {
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
        PlayerXp playerXp = MCMMO.getPlayerXp(player);
        ChatWriter chat = new ChatWriter(player);

        int powerLevel = 0;

        for(Skill skill : Skill.values()) {
            XPWrapper skillXp = playerXp.getSkillXp(skill);
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
        List<String> completions = new LinkedList<>();

        if (parts.length <= 1) {
            completions.add("help");
            completions.add("inspect");
            completions.add("skills");
            completions.add("stats");

            for(Skill skill : Skill.values()) {
                completions.add(skill.name().toLowerCase());
            }

            if (parts.length == 1) {
                completions.removeIf(completion -> !completion.startsWith(parts[0]));
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
