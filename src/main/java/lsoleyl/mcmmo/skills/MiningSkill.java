package lsoleyl.mcmmo.skills;

import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;

public class MiningSkill implements ISkill {

    @Override
    public void printDescription(ChatWriter chat, int level) {
        chat.writeMessage(ChatFormat.formatCaption("MINING"));
        chat.writeMessage(ChatFormat.formatXpGain("Mining Stone & Ore"));
        chat.writeMessage(ChatFormat.formatCaption("EFFECTS"));
    }
}
