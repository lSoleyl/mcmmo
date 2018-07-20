package lsoleyl.mcmmo.data;

import lsoleyl.mcmmo.skills.Skill;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** This class represents the player's data, which is relevant to mcmmo. This is basically only the xp for each skill
 *  as the level can be derived from the xp and the progression curve. The use of a Map might not be the fastest way
 *  but the most flexible when adding new skills.
 */
public class PlayerXp {
    UUID playerUid;
    String playerName;
    Map<Skill, Long> skillMap = new HashMap<>();

    PlayerXp(EntityPlayer player) {
        this.playerUid = player.getUniqueID();
        this.playerName = player.getDisplayName();
    }

    long get(Skill skill) {
        return skillMap.getOrDefault(skill, 0L);
    }

    void set(Skill skill, long xp) {
        skillMap.put(skill, xp);
    }
}
