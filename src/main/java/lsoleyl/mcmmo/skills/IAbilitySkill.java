package lsoleyl.mcmmo.skills;

import net.minecraft.entity.player.EntityPlayerMP;

/** This interface should be implemented by all skills, implementing an active ability, which needs to
 *  be manually activated. These hook methods are called by the cooldown manager, to which an ability should be registered
 *  upon preparation... The ability must not be re-registered after activating the ability.
 *
 */
public interface IAbilitySkill extends ISkill {
    void onPrepare(EntityPlayerMP player);

    /** Called when ability has not been activated within the prepare duration.
     */
    void onPrepareTimeout(EntityPlayerMP player);

    /** Should be called when the ability is activated.
     */
    void onAbilityActivate(EntityPlayerMP player);

    /** Called after the ability wears out
     */
    void onAbilityWearOut(EntityPlayerMP player);
}
