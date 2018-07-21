package lsoleyl.mcmmo.data;

import lsoleyl.mcmmo.skills.Skill;

import java.util.HashMap;
import java.util.Map;

/** This class represents the player's data, which is relevant to mcmmo. This is basically only the xp for each skill
 *  as the level can be derived from the xp and the progression curve. The use of a Map might not be the fastest way
 *  but the most flexible when adding new skills.
 */
public class PlayerXp {
    Map<Skill, Long> skillMap = new HashMap<>();

    PlayerXp() {}

    public long get(Skill skill) {
        return skillMap.getOrDefault(skill, 0L);
    }

    public void set(Skill skill, long xp) {
        skillMap.put(skill, xp);
    }

    @Override
    public String toString() {
        return "XP(skills=" + skillMap.toString() + ")";
    }
}
