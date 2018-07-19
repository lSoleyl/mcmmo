package lsoleyl.mcmmo;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = MCMMO.MODID, version = MCMMO.VERSION, name = MCMMO.NAME)
public class MCMMO
{
    public static final String MODID = "mcmmo";
    public static final String VERSION = "1.0";
    public static final String NAME = "MCMMO - Forge";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
}
