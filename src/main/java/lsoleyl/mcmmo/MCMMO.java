package lsoleyl.mcmmo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import lsoleyl.mcmmo.commands.MCMMOCommand;
import lsoleyl.mcmmo.data.DataStorage;
import lsoleyl.mcmmo.data.PlayerXp;
import lsoleyl.mcmmo.events.AttackListener;
import lsoleyl.mcmmo.events.BlockListener;
import lsoleyl.mcmmo.skills.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.MinecraftForge;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = MCMMO.MODID, version = MCMMO.VERSION, name = MCMMO.NAME)
public class MCMMO
{
    public static final String MODID = "mcmmo";
    public static final String VERSION = "1.0";
    public static final String NAME = "MCMMO - Forge";

    private static MCMMO instance;
    private DataStorage dataStorage;

    public static PlayerXp getPlayerXp(EntityPlayer player) {
        return instance.dataStorage.get(player);
    }

    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        instance = this;

        System.out.println("---INIT----");
        if (FMLCommonHandler.instance().getSide().isClient()) {
            // This is a server only mod, so we won't do any initialization on the client side
            return;
        }

        // Load saved data (if any)
        dataStorage = DataStorage.Initialize();
        MinecraftForge.EVENT_BUS.register(new BlockListener());
        MinecraftForge.EVENT_BUS.register(new AttackListener());


        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        System.out.println("--- ServerStarting ---");
        if (FMLCommonHandler.instance().getSide().isServer()) {
            event.registerServerCommand(new MCMMOCommand());

            //TODO enable this check once all Skill classes are implemented
            // Make sure, each skill is registered
            //SkillRegistry.getInstance().validate();
        }
    }
}
