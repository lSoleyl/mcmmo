package lsoleyl.mcmmo.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

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

    /** Casts the given Entity into EntityPlayerMP if the entity is actually a player, not null and
     *  not a ComputerCraft turtle (which should not be affected by MCMMO).
     *
     * @param entity the entity to convert into a player
     * @return either the player entity or null if it isn't a player
     */
    public static EntityPlayerMP getPlayer(Entity entity) {
        if (entity != null && entity instanceof EntityPlayerMP && !entity.getClass().getSimpleName().equals("TurtlePlayer")) {
            return (EntityPlayerMP) entity;
        }

        return null;
    }

    /** Knocks back the entity from the source away... This doesn't work with players as their movements are calculated
     *  on the client's side...
     *
     * @param source who knocks back?
     * @param target what entity to knock back
     */
    public static void knockBack(Entity source, EntityLivingBase target) {
        double dx = target.posX - source.posX;
        double dz = target.posZ - source.posZ;

        target.knockBack(source/*unused*/, 0/*unused*/, -dx, -dz);
    }

    public static List getEntitiesAround(Entity entity, float radius) {
        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(entity.posX - radius, entity.posY - radius, entity.posZ - radius,
                                                                 entity.posX + radius, entity.posY + radius,entity.posZ + radius);
        return entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, boundingBox);
    }
}
