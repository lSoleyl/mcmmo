package lsoleyl.mcmmo.commands;

import com.google.common.collect.ImmutableList;
import lsoleyl.mcmmo.MCMMO;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.Skill;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.LinkedList;
import java.util.List;

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

        if (arguments.size() == 0) {
            // only /mcmmo has been run -> treat it like help
            helpCommand(player, arguments);
            return;
        }


        switch(arguments.get(0)) {
            case "help": helpCommand(player, arguments.subList(1, arguments.size())); break;
            case "skills": skillsCommand(player); break;

            //TODO implement remaining commands, including skill commands

            default: player.addChatMessage(new ChatComponentText("Unknown /mcmmo command: " + arguments.get(0)));
        }
    }

    private void helpCommand(EntityPlayerMP player, ImmutableList<String> arguments) {
        //TODO implement help including the skill help

        if (arguments.size() == 0) {
            //TODO print general help, like which topics and skills are available
            player.addChatMessage(new ChatComponentText("Generic help"));
        } else {
            String topic = arguments.get(0);
            player.addChatMessage(new ChatComponentText("Help for " + topic));
            //TODO print help for that topic/skill
        }
    }

    private void skillsCommand(EntityPlayerMP player) {
        PlayerXp playerXp = MCMMO.getPlayerXp(player);

        //TODO format the messages better
        for(Skill skill : Skill.values()) {
            player.addChatMessage(new ChatComponentText(skill + ": " + new XPWrapper(playerXp, skill)));
        }
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
