package lsoleyl.mcmmo;

import lsoleyl.mcmmo.events.BlockListener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = MCMMO.MODID, version = MCMMO.VERSION, name = MCMMO.NAME)
public class MCMMO
{
    public static final String MODID = "mcmmo";
    public static final String VERSION = "1.0";
    public static final String NAME = "MCMMO - Forge";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //TODO find out whether we run on a server and only then register the event handlers
        MinecraftForge.EVENT_BUS.register(new BlockListener());



        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
}
