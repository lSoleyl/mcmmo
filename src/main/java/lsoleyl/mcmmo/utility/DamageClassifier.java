package lsoleyl.mcmmo.utility;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;

/** This class is used to better categorize damage sources to know which skills have to be applied.
 */
public class DamageClassifier {
    public final boolean isDodgeable;
    public final boolean isArrow;
    public final boolean isDrown;
    public final boolean isFall;
    public final boolean isPoison;
    public final boolean isWither;
    public final boolean isFire;
    public final boolean isExplosion;

    public final boolean isEnvironment; //wallstuck, out of world, hunger


    private DamageClassifier(DamageSource source) {
        isArrow = source.isProjectile() && source.getSourceOfDamage() instanceof EntityArrow;
        isDrown = (source == DamageSource.drown);
        isFall = (source == DamageSource.fall);
        isPoison = (source == DamageSource.magic);
        isWither = (source == DamageSource.wither);
        isFire = source.isFireDamage();
        isExplosion = source.isExplosion();

        isEnvironment = source == DamageSource.cactus
                        || source == DamageSource.inWall
                        || source == DamageSource.starve
                        || source == DamageSource.outOfWorld
                        || source == DamageSource.anvil
                        || source == DamageSource.fallingBlock;

        isDodgeable = !isArrow && !isDrown && !isFall && !isPoison && !isWither && !isFire && !isExplosion && !isEnvironment;
    }

    public static DamageClassifier classify(DamageSource source) {
        return new DamageClassifier(source);
    }
}
