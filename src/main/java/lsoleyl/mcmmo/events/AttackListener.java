package lsoleyl.mcmmo.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryDefaulted;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.server.command.ForgeCommand;

import java.util.ArrayList;

public class AttackListener {
    //TODO assign correct priority... most probably the lowest
    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        //TODO this may be helpful for dodge skill
        //System.out.println(event.entity + " got damaged by " + print(event.source));
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        //TODO this event is being evaluated to determine the actual damage to apply to the armor and health of the entity
        //TODO so we need to check source and target and possibly apply any relevant combat skills
        //TODO we also have to differentiate between mobs and players
    }

    public String print(DamageSource source) {
        return "DamageSource(Type=" + source.damageType + ",Source=" + source.getSourceOfDamage() + ")";
    }

    public String print(EntityPlayer player) {


        return "Player(name=" + player.getDisplayName() + ",uuid=" + player.getUniqueID() + ",currentItem=" + print(player.getCurrentEquippedItem()) + ",usingItem=" + player.isUsingItem() + ")";

    }

    public String print(ItemStack item) {
        String names = "";
        for(int id : OreDictionary.getOreIDs(item)) {
            names += OreDictionary.getOreName(id) + ",";
        }

        //TODO the unlocalized name seems to always contain sword for swords... etc.
        return "Item(dispName=" + item.getDisplayName() + ",unlocalName=" + item.getUnlocalizedName() + ",names=[" + names + "])";
    }


    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        System.out.println(print(event.entityPlayer) + " attacked " + event.target);
    }

    @SubscribeEvent
    public void itemUsed(PlayerUseItemEvent event) {
        System.out.println(print(event.entityPlayer) + " used item " + event.item);
    }

}
