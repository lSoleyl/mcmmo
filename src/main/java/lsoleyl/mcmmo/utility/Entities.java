package lsoleyl.mcmmo.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;

public class Entities {
    public static boolean isPeacefulTowards(Entity entity, EntityPlayer player) {
        if (entity instanceof IMob) {
            return false; // all mobs are harmful
        }

        if (entity instanceof EntityTameable) {
            EntityTameable tamedEntity = (EntityTameable) entity;
            return (tamedEntity.isTamed() && tamedEntity.getOwner() == player); // only not owned animals are potentially harmful
        }

        if (entity instanceof IAnimals) {
            return true; // animals, that aren't mobs are peaceful
        }

        if (entity instanceof EntityPlayer) {
            return false; // players are not considered peaceful
        }

        return false; // all unknown entities are harmful
    }

    public static void knockBack(Entity source, EntityLivingBase target) {
        double dx = source.posX - target.posX;
        double dz = source.posZ - target.posZ;

        target.knockBack(source/*unused*/, 0/*unused*/, dx, dz);
    }
}
