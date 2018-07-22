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
import net.minecraft.util.ChatComponentText;

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
        //TODO implement help including the skill help
        ChatWriter chat = new ChatWriter(player);

        if (arguments.size() == 0) {
            //TODO print general help, like which topics and skills are available
            chat.writeMessage("Generic help");
        } else {
            String topic = arguments.get(0);

            Optional<Skill> skill = Skill.getByName(topic);
            if (skill.isPresent()) {
                SkillRegistry.getInstance().getSkill(skill.get()).printHelp(chat);
                return;
            }

            chat.writeMessage("Help for " + topic);
            //TODO print help for that topic (which topics are there?)
        }
    }

    /** Lists info for a specific skill regarding the player
     *  /mcmmo <skill>
     */
    private void skillCommand(EntityPlayerMP player, Skill skill) {
        XPWrapper xp = new XPWrapper(MCMMO.getPlayerXp(player), skill);
        SkillRegistry.getInstance().getSkill(skill).printDescription(new ChatWriter(player), xp.getLevel());
    }

    /** Lists the current level of each skill and the total power level
     *  /mcmmo skills
     */
    private void skillsCommand(EntityPlayerMP player) {
        PlayerXp playerXp = MCMMO.getPlayerXp(player);
        ChatWriter chat = new ChatWriter(player);

        int powerLevel = 0;

        for(Skill skill : Skill.values()) {
            XPWrapper skillXp = new XPWrapper(playerXp, skill);
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
    public List addTabCompletionOptions(ICommandSender sender, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        //TODO this should be true for /mcmmo inspect <player>
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
