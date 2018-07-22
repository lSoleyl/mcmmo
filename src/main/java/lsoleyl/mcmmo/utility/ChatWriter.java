package lsoleyl.mcmmo.utility;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

/** This class wraps the EntityPlayerMP chat message interface to more easily write chat messages
 */
public class ChatWriter {
    private EntityPlayerMP player;

    public ChatWriter(EntityPlayerMP player) {
        this.player = player;
    }

    public void writeMessage(String message) {
        player.addChatMessage(new ChatComponentText(message));
    }
}
