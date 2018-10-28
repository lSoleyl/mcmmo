package lsoleyl.mcmmo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import lsoleyl.mcmmo.commands.MCMMOCommand;
import lsoleyl.mcmmo.data.DataStorage;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.events.AbilityListener;
import lsoleyl.mcmmo.events.AttackListener;
import lsoleyl.mcmmo.events.BlockListener;
import lsoleyl.mcmmo.events.TickListener;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.skills.SkillRegistry;
import lsoleyl.mcmmo.utility.ChatFormat;
import lsoleyl.mcmmo.utility.ChatWriter;
import lsoleyl.mcmmo.utility.Optional;
import lsoleyl.mcmmo.utility.Sound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

@Mod(modid = MCMMO.MODID, version = MCMMO.VERSION, name = MCMMO.NAME)
public class MCMMO
{
    public static final String MODID = "mcmmo";
    public static final String VERSION = "1.0";
    public static final String NAME = "MCMMO - Forge";

    public static long tickCount = 0; // The number of ticks since the server start

    private static MCMMO instance;
    private DataStorage dataStorage;

    public static PlayerXp getPlayerXp(EntityPlayer player) {
        return instance.dataStorage.get(player);
    }

    public static Optional<PlayerXp> getPlayerXp(String playerName) {
        return instance.dataStorage.get(playerName);
    }

    public static Set<String> getPlayerNames() { return instance.dataStorage.getPlayerNames(); }

    /** This function should be called to handle leveling up.. This will simply print a short message to the user's chat
     *
     * @param player the player who may have leveled up
     * @param skill the affected skill
     * @param newLevel the result of XPWrapper.addXp
     */
    public static void playerLevelUp(EntityPlayerMP player, Skill skill, Optional<Integer> newLevel) {
        if (newLevel.isPresent()) {
            new ChatWriter(player).writeMessage(ChatFormat.formatLevelUp(skill, newLevel.get()));
            Sound.LEVEL_UP.playAt(player, 0.5f);
        }
    }

    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        // We cannot really initialize anything here as we don't yet know the logical side we are running on. (SP)
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        System.out.println("--- ServerStarting ---");
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            // Load saved data (if any)
            dataStorage = DataStorage.Initialize();

            // register event listeners
            MinecraftForge.EVENT_BUS.register(new BlockListener());
            MinecraftForge.EVENT_BUS.register(new AttackListener());
            MinecraftForge.EVENT_BUS.register(new AbilityListener());

            FMLCommonHandler.instance().bus().register(new TickListener());

            // Register command
            event.registerServerCommand(new MCMMOCommand());

            // Make sure, each skill is registered
            SkillRegistry.getInstance().validate();
        }
    }
}
