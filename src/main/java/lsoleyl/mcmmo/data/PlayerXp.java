package lsoleyl.mcmmo.data;

import com.google.gson.*;
import lsoleyl.mcmmo.experience.XPWrapper;
import lsoleyl.mcmmo.skills.Skill;
import lsoleyl.mcmmo.utility.Optional;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** This class represents the player's data, which is relevant to mcmmo. This is basically only the xp for each skill
 *  as the level can be derived from the xp and the progression curve. The use of a Map might not be the fastest way
 *  but the most flexible when adding new skills.
 */
public class PlayerXp {
    private HashMap<Skill, Long> skillMap = new HashMap<Skill, Long>();

    // keep wrapped playerXp in a map to optimize lookup and prevent creating them all the time
    private transient Map<Skill, XPWrapper> wrapperMap = new HashMap<Skill, XPWrapper>();

    PlayerXp() {}

    public XPWrapper getSkillXp(Skill skill) {
        if (!wrapperMap.containsKey(skill)) {
            wrapperMap.put(skill, new XPWrapper(this, skill));
        }

        return wrapperMap.get(skill);
    }

    public long get(Skill skill) {
        if (skillMap.containsKey(skill)) {
            return skillMap.get(skill);
        }

        return 0L;
    }

    /** Returns the player's powerlevel, which is the level of all skills summed up
     */
    public int getPowerLevel() {
        int powerLevel = 0;
        for (Skill skill : Skill.values()) {
            powerLevel += getSkillXp(skill).getLevel();
        }

        return powerLevel;
    }

    public void set(Skill skill, long xp) {
        skillMap.put(skill, xp);
    }

    @Override
    public String toString() {
        return "XP(skills=" + skillMap.toString() + ")";
    }

    public static JsonDeserializer<PlayerXp> deserializer() {
        return new JsonDeserializer<PlayerXp>() {

            @Override
            public PlayerXp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (!json.isJsonObject()) {
                    throw new JsonParseException("Cannot deserialize non-object into PlayerXp object");
                }

                PlayerXp result = new PlayerXp();
                JsonObject object = json.getAsJsonObject();
                if (object.has("skillMap")) {
                    if (object.get("skillMap").isJsonObject()) {
                        JsonObject skillMap = object.getAsJsonObject("skillMap");
                        for(Map.Entry<String, JsonElement> entry : skillMap.entrySet()) {
                            // Enter each entry into the hashmap
                            Optional<Skill> skill = Skill.getByName(entry.getKey());
                            if (skill.isPresent()) {
                                result.skillMap.put(skill.get(), entry.getValue().getAsLong());
                            }
                        }
                    }
                }

                return result;
            }
        };
    }
}
